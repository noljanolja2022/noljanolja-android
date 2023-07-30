package com.noljanolja.android.features.home.chat

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.EmojiSupportMatch
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.noljanolja.android.R
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.features.home.chat.components.ChatInput
import com.noljanolja.android.features.home.chat.components.ChatMessageMenuDialog
import com.noljanolja.android.features.home.chat.components.ChatMessageReactionsDialog
import com.noljanolja.android.features.home.chat.components.ChatReactions
import com.noljanolja.android.features.home.chat.components.ClickableMessage
import com.noljanolja.android.features.home.chat.components.GridContent
import com.noljanolja.android.ui.composable.CommonTopAppBar
import com.noljanolja.android.ui.composable.FullSizeWithBottomSheet
import com.noljanolja.android.ui.composable.InfiniteListHandler
import com.noljanolja.android.ui.composable.OvalAvatar
import com.noljanolja.android.ui.composable.ScaffoldWithUiState
import com.noljanolja.android.ui.composable.SizeBox
import com.noljanolja.android.ui.composable.VerticalDivider
import com.noljanolja.android.ui.theme.BlueGray
import com.noljanolja.android.ui.theme.colorMyChatText
import com.noljanolja.android.util.*
import com.noljanolja.core.conversation.domain.model.*
import com.noljanolja.core.media.domain.model.Sticker
import com.noljanolja.core.utils.Const
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ChatScreen(
    savedStateHandle: SavedStateHandle,
    conversationId: Long,
    userIds: List<String>,
    title: String,
    viewModel: ChatViewModel = getViewModel { parametersOf(conversationId, userIds, title) },
) {
    val context = LocalContext.current
    val isLeave = savedStateHandle.get<Boolean>("leave") ?: false
    LaunchedEffect(key1 = isLeave) {
        if (isLeave) {
            savedStateHandle.remove<Boolean>("leave")
            viewModel.handleEvent(ChatEvent.GoBack)
        }
    }
    LaunchedEffect(key1 = viewModel.errorFlow) {
        viewModel.errorFlow.collectLatest {
            context.showError(it)
        }
    }
    LaunchedEffect(key1 = conversationId) {
        viewModel.handleEvent(ChatEvent.LoadMedia)
    }
    val chatUiState by viewModel.chatUiStateFlow.collectAsStateWithLifecycle()
    val loadedMedia by viewModel.loadedMediaFlow.collectAsStateWithLifecycle()
    val reactIcons by viewModel.reactIconsFlow.collectAsStateWithLifecycle()
    ChatScreenContent(
        chatUiState = chatUiState,
        mediaList = loadedMedia,
        reactIcons = reactIcons,
        handleEvent = viewModel::handleEvent,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ChatScreenContent(
    chatUiState: UiState<Conversation>,
    reactIcons: List<ReactIcon>,
    mediaList: List<Pair<Uri, Long?>>,
    handleEvent: (ChatEvent) -> Unit,
) {
    val scrollState = rememberLazyListState()
    val firstItemVisible = remember { derivedStateOf { scrollState.firstVisibleItemIndex } }
    var stickerSelected by remember {
        mutableStateOf<Sticker?>(null)
    }

    val context = LocalContext.current

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val scope = rememberCoroutineScope()
    val conversation = chatUiState.data ?: return
    val selectedMedia = remember { mutableStateListOf<Uri>() }
    var tempReplyMessage by remember {
        mutableStateOf<Message?>(null)
    }
    val bottomSheetState = rememberBottomSheetScaffoldState()
    val chatFocusRequester = remember { FocusRequester() }

    FullSizeWithBottomSheet(modalSheetState = bottomSheetState, sheetContent = {
        Column(
            modifier = Modifier
                .heightIn(max = (LocalConfiguration.current.screenHeightDp * 0.9f).dp)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Row(
                modifier = Modifier
                    .height(54.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(id = R.string.common_all),
                    style = MaterialTheme.typography.titleMedium
                )
                IconButton(onClick = {
                    scope.launch {
                        bottomSheetState.bottomSheetState.collapse()
                    }
                }) {
                    Icon(Icons.Default.Close, contentDescription = null)
                }
            }
            GridContent(
                mediaList = mediaList,
                selectedMedia = selectedMedia,
                onMediaSelect = { mediaSelect: List<Uri>, isAdd: Boolean ->
                    when (isAdd) {
                        true -> selectedMedia.addAll(mediaSelect)
                        false -> selectedMedia.removeAll(mediaSelect)
                    }
                }
            )
        }
    }) {
        ScaffoldWithUiState(
            uiState = chatUiState,
            topBar = {
                CommonTopAppBar(
                    title = conversation.getDisplayTitle(),
                    leadingTitle = conversation.getSingleReceiver()?.let {
                        {
                            OvalAvatar(
                                user = it,
                                size = 34.dp,
                                modifier = Modifier.padding(end = 10.dp)
                            )
                        }
                    },
                    centeredTitle = conversation.type == ConversationType.GROUP,
                    onBack = { handleEvent(ChatEvent.GoBack) },
                    actions = {
                        ChatBarActions(onChatOption = {
                            handleEvent(ChatEvent.ChatOptions)
                        })
                    }
                )
            },
            content = {
                Column(
                    Modifier
                        .fillMaxSize()
                        .nestedScroll(scrollBehavior.nestedScrollConnection)
                ) {
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.BottomEnd,
                    ) {
                        MessageList(
                            conversationId = conversation.id,
                            messages = conversation.messages,
                            conversationType = conversation.type,
                            modifier = Modifier.fillMaxSize(),
                            scrollState = scrollState,
                            onMessageReply = {
                                tempReplyMessage = it
                                chatFocusRequester.requestFocus()
                            },
                            reactIcons = reactIcons,
                            handleEvent = handleEvent
                        )
                        if (firstItemVisible.value != 0) {
                            ScrollToNewestMessageButton(onClick = {
                                scope.launch {
                                    scrollState.scrollToItem(0)
                                }
                            })
                        }
                    }
                    tempReplyMessage?.let { mess ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 0.5.dp,
                                    color = MaterialTheme.colorScheme.primaryContainer
                                )
                                .padding(
                                    horizontal = 16.dp,
                                    vertical = 6.dp
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Reply to ${mess.sender.name}",
                                    style = MaterialTheme.typography.labelSmall
                                )
                                Text(
                                    text = mess.message,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    style = TextStyle(
                                        fontSize = 10.sp
                                    )
                                )
                            }
                            Icon(
                                Icons.Default.Close,
                                contentDescription = null,
                                modifier = Modifier.clickable {
                                    tempReplyMessage = null
                                }
                            )
                        }
                    }
                    ChatInput(
                        onMessageSent = { message, type, attachments ->
                            val sendMessage = when (type) {
                                MessageType.GIF,
                                MessageType.PLAINTEXT,
                                -> {
                                    if (message.isNotBlank()) {
                                        Message(
                                            message = message.trim(),
                                            type = type,
                                        )
                                    } else {
                                        null
                                    }
                                }

                                MessageType.DOCUMENT, MessageType.PHOTO -> {
                                    Message(
                                        message = message.trim(),
                                        type = type,
                                        attachments = attachments.map {
                                            val fileInfo = context.loadFileInfo(it)
                                            MessageAttachment(
                                                name = "",
                                                originalName = fileInfo.name,
                                                type = fileInfo.contentType,
                                                size = fileInfo.contents.size.toLong(),
                                                contents = fileInfo.contents,
                                                localPath = fileInfo.path.toString(),
                                            )
                                        },
                                    )
                                }

                                MessageType.STICKER -> {
                                    Message(
                                        message = message,
                                        stickerUrl = message,
                                        type = MessageType.STICKER,
                                    )
                                }

                                else -> null
                            }

                            sendMessage?.let {
                                handleEvent(
                                    ChatEvent.SendMessage(
                                        message = it,
                                        replyToMessageId = tempReplyMessage?.id
                                    )
                                )
                                tempReplyMessage = null
                            }
                        },
                        modifier = Modifier
                            .navigationBarsPadding()
                            .imePadding(),
                        selectedMedia = selectedMedia,
                        resetScroll = { scope.launch { scrollState.scrollToItem(0) } },
                        mediaList = mediaList,
                        focusRequester = chatFocusRequester,
                        loadMedia = { handleEvent(ChatEvent.LoadMedia) },
                        openPhoneSetting = { handleEvent(ChatEvent.OpenPhoneSettings) },
                        onShowSticker = {
                            stickerSelected = it
                        },
                        onChangeSelectMedia = {
                            selectedMedia.clear()
                            selectedMedia.addAll(it)
                        },
                        onOpenFullImages = {
                            scope.launch {
                                bottomSheetState.bottomSheetState.expand()
                            }
                        }
                    )
                }
                stickerSelected?.let {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.7F)),
                        contentAlignment = Alignment.Center
                    ) {
                        SubcomposeAsyncImage(
                            ImageRequest.Builder(context = LocalContext.current)
                                .setAnimated(true)
                                .data(it.imageFile)
                                .build(),
                            contentDescription = null,
                            modifier = Modifier.size(150.dp)
                        )
                    }
                }
            }
        )
    }
}

@Composable
fun ChatBarActions(onChatOption: () -> Unit) {
    IconButton(onClick = onChatOption) {
        Icon(Icons.Default.Menu, contentDescription = null)
    }
}

@Composable
private fun MessageList(
    conversationId: Long,
    messages: List<Message>,
    conversationType: ConversationType,
    scrollState: LazyListState,
    reactIcons: List<ReactIcon>,
    modifier: Modifier = Modifier,
    onMessageReply: (Message) -> Unit,
    handleEvent: (ChatEvent) -> Unit,
) {
    LaunchedEffect(messages.firstOrNull()?.localId.orEmpty()) {
        // Wait for the LazyColumn to recompose with the new data
        snapshotFlow { scrollState.layoutInfo.visibleItemsInfo }
            .filter { it.isNotEmpty() }
            .first()
        if (messages.firstOrNull()?.sender?.isMe == true) {
            scrollState.scrollToItem(0)
        }
    }

    var topOffset by remember { mutableStateOf(0F) }
    var height by remember { mutableStateOf(0F) }
    Box(modifier = modifier) {
        LazyColumn(
            reverseLayout = true,
            state = scrollState,
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged {
                    height = it.height.toFloat()
                }
                .onGloballyPositioned { coordinates ->
                    topOffset = coordinates.positionInRoot().y
                }
        ) {
            for (index in messages.indices) {
                val prevMessage = messages.getOrNull(index - 1)
                val prevSender = prevMessage?.sender
                val nextMessage = messages.getOrNull(index + 1)
                val nextSender = nextMessage?.sender
                val message = messages[index]
                val sender = message.sender
                val isFirstMessageBySender = prevSender?.id != sender.id
                val isLastMessageBySender = nextSender?.id != sender.id

                val sameDateWithNextMessage =
                    nextMessage != null && message.createdAt.isSameDate(nextMessage.createdAt)
                val sameDateWithPreviousMessage =
                    prevMessage != null && message.createdAt.isSameDate(prevMessage.createdAt)
                val isFirstMessageByAuthorSameDay = isFirstMessageBySender ||
                    sameDateWithNextMessage && !sameDateWithPreviousMessage ||
                    nextMessage == null
                val isLastMessageByAuthorSameDay = isLastMessageBySender ||
                    !sameDateWithNextMessage && (sameDateWithPreviousMessage || prevMessage == null)

                if (prevMessage != null && !message.createdAt.isSameDate(prevMessage.createdAt)) {
                    item {
                        DayHeader(prevMessage.createdAt.chatMessageHeaderDate(context = LocalContext.current))
                    }
                }

                item(key = message.localId) {
                    MessageRow(
                        conversationId = conversationId,
                        message = message,
                        conversationType = conversationType,
                        isFirstMessageByAuthorSameDay = isFirstMessageByAuthorSameDay,
                        isLastMessageByAuthorSameDay = isLastMessageByAuthorSameDay,
                        handleEvent = handleEvent,
                        reactIcons = reactIcons,
                        onMessageReply = onMessageReply
                    )
                }

                if (nextMessage == null) {
                    item {
                        DayHeader(message.createdAt.chatMessageHeaderDate(context = LocalContext.current))
                    }
                }
            }
        }
        InfiniteListHandler(scrollState, onLoadMore = {
            handleEvent(ChatEvent.LoadMoreMessages)
        })
    }
}

@Composable
private fun DayHeader(dayString: String) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .padding(top = 30.dp)
                .background(
                    BlueGray,
                    shape = RoundedCornerShape(7.dp)
                )
        ) {
            Text(
                text = dayString,
                modifier = Modifier
                    .padding(horizontal = 10.dp, vertical = 5.dp)
                    .align(Alignment.Center),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.background
                )
            )
        }
    }
}

@Composable
fun MessageRow(
    conversationId: Long,
    message: Message,
    conversationType: ConversationType,
    isFirstMessageByAuthorSameDay: Boolean,
    isLastMessageByAuthorSameDay: Boolean,
    reactIcons: List<ReactIcon>,
    showReaction: Boolean = true,
    onMessageReply: (Message) -> Unit = {},
    handleEvent: (ChatEvent) -> Unit,
) {
    val context = LocalContext.current
    val spaceBetweenAuthors =
        if (isLastMessageByAuthorSameDay) Modifier.padding(top = 8.dp) else Modifier
    when (message.type) {
        MessageType.EVENT_JOINED -> {
            MessageEvent(
                stringResource(
                    id = R.string.chat_message_event_joined,
                    message.sender.name,
                    message.joinParticipants.joinToString(", ") { it.name }
                ),
            )
        }

        MessageType.EVENT_LEFT -> {
            MessageEvent(
                stringResource(
                    id = R.string.chat_message_event_left,
                    message.leftParticipants.joinToString(", ") { it.name }
                ),
            )
        }

        MessageType.EVENT_UPDATED -> {
            MessageEvent(
                stringResource(
                    id = R.string.chat_message_event_updated,
                    message.sender.name,
                    message.message
                ),
            )
        }

        else -> {
            Row(modifier = spaceBetweenAuthors) {
                SizeBox(width = 16.dp)
                if (conversationType == ConversationType.GROUP) {
                    if (isLastMessageByAuthorSameDay && !message.sender.isMe) {
                        // Avatar
                        OvalAvatar(
                            size = 32.dp,
                            user = message.sender,
                            modifier = Modifier
                                .clickable {
                                    handleEvent(
                                        ChatEvent.NavigateToProfile(
                                            message.sender
                                        )
                                    )
                                }
                                .padding(end = 5.dp)
                        )
                    } else {
                        // Space under avatar
                        Spacer(modifier = Modifier.width(37.dp))
                    }
                }
                AuthorAndTextMessage(
                    conversationId = conversationId,
                    message = message,
                    conversationType = conversationType,
                    isFirstMessageByAuthorSameDay = isFirstMessageByAuthorSameDay,
                    isLastMessageByAuthorSameDay = isLastMessageByAuthorSameDay,
                    reactIcons = reactIcons,
                    showReaction = showReaction,
                    onMessageReply = onMessageReply,
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .weight(1f),
                    handleEvent = handleEvent
                )
                val modifier = Modifier
                    .width(20.dp)
                    .align(Alignment.Bottom)
                when (message.status) {
                    MessageStatus.FAILED -> {
                        Icon(
                            Icons.Outlined.Error,
                            contentDescription = null,
                            modifier = modifier,
                            tint = MaterialTheme.colorScheme.errorContainer
                        )
                    }

                    else -> if (message.sender.isMe) {
                        Box(modifier = modifier) {
                            message.seenUsers.filter { !it.isMe }.takeIf { it.isNotEmpty() }
                                ?.forEachIndexed { index, userSeen ->
                                    AsyncImage(
                                        ImageRequest.Builder(context = context)
                                            .data(userSeen.getAvatarUrl())
                                            .placeholder(R.drawable.placeholder_avatar)
                                            .error(R.drawable.placeholder_avatar)
                                            .fallback(R.drawable.placeholder_avatar)
                                            .build(),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .padding(start = (6 * index).dp)
                                            .size(13.dp)
                                            .clip(RoundedCornerShape(13.dp)),
                                        contentScale = ContentScale.FillBounds,
                                    )
                                }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AuthorAndTextMessage(
    conversationId: Long,
    message: Message,
    conversationType: ConversationType,
    isFirstMessageByAuthorSameDay: Boolean,
    isLastMessageByAuthorSameDay: Boolean,
    reactIcons: List<ReactIcon>,
    modifier: Modifier = Modifier,
    showReaction: Boolean,
    onMessageReply: (Message) -> Unit,
    handleEvent: (ChatEvent) -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = if (message.sender.isMe) Alignment.End else Alignment.Start
    ) {
        val configuration = LocalConfiguration.current
        val maxChatItemWidth = (configuration.screenWidthDp * 0.66).dp
        val maxChatItemHeight =
            if (message.type == MessageType.PLAINTEXT) {
                Dp.Unspecified
            } else {
                max(
                    (configuration.screenHeightDp * 0.33).dp,
                    maxChatItemWidth
                )
            }
        if (isLastMessageByAuthorSameDay && !message.sender.isMe) {
            Spacer(modifier = Modifier.height(12.dp))
        }
        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            Box(
                modifier = Modifier
                    .widthIn(0.dp, maxChatItemWidth)
                    .heightIn(0.dp, maxChatItemHeight)
            ) {
                ChatItemBubble(
                    conversationId = conversationId,
                    message = message,
                    conversationType = conversationType,
                    isFirstMessageByAuthorSameDay = isFirstMessageByAuthorSameDay,
                    isLastMessageByAuthorSameDay = isLastMessageByAuthorSameDay,
                    showReaction = showReaction,
                    reactIcons = reactIcons,
                    onMessageReply = onMessageReply,
                    handleEvent = handleEvent
                )
            }
        }
        if (isFirstMessageByAuthorSameDay) {
            Spacer(modifier = Modifier.height(10.dp))
        } else {
            Spacer(modifier = Modifier.height(3.dp))
        }
    }
}

@Composable
private fun MessageTimestamp(timestamp: String) {
    Text(
        timestamp,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.6F),
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 6.dp)
    )
}

object MessageChatBubbleShape {
//    private val TOP_MESSAGE = RoundedCornerShape(18.dp, 18.dp, 18.dp, 6.dp)
//    private val CENTER_MESSAGE = RoundedCornerShape(6.dp, 18.dp, 18.dp, 6.dp)
//    private val BOTTOM_MESSAGE = RoundedCornerShape(6.dp, 18.dp, 18.dp, 18.dp)
//
//    private val SINGLE_MESSAGE = RoundedCornerShape(18.dp)
//
//    private val TOP_MY_MESSAGE = RoundedCornerShape(18.dp, 18.dp, 6.dp, 18.dp)
//    private val CENTER_MY_MESSAGE = RoundedCornerShape(18.dp, 6.dp, 6.dp, 18.dp)
//    private val BOTTOM_MY_MESSAGE = RoundedCornerShape(18.dp, 6.dp, 18.dp, 18.dp)

    private val TOP_MESSAGE = RoundedCornerShape(10.dp)
    private val CENTER_MESSAGE = RoundedCornerShape(10.dp)
    private val BOTTOM_MESSAGE = RoundedCornerShape(10.dp)

    private val SINGLE_MESSAGE = RoundedCornerShape(10.dp)

    private val TOP_MY_MESSAGE = RoundedCornerShape(10.dp)
    private val CENTER_MY_MESSAGE = RoundedCornerShape(10.dp)
    private val BOTTOM_MY_MESSAGE = RoundedCornerShape(10.dp)
    fun getChatBubbleShape(
        isFirstMessage: Boolean,
        isLastMessage: Boolean,
        isMe: Boolean,
    ): RoundedCornerShape {
        return if (isMe) {
            getChatBubbleShapeOfMine(isFirstMessage, isLastMessage)
        } else {
            getChatBubbleShapeOfPartner(isFirstMessage, isLastMessage)
        }
    }

    private fun getChatBubbleShapeOfPartner(
        isFirstMessage: Boolean,
        isLastMessage: Boolean,
    ): RoundedCornerShape {
        return when {
            isFirstMessage && isLastMessage -> SINGLE_MESSAGE
            !isFirstMessage && !isLastMessage -> CENTER_MESSAGE
            isFirstMessage -> BOTTOM_MESSAGE
            else -> TOP_MESSAGE
        }
    }

    private fun getChatBubbleShapeOfMine(
        isFirstMessage: Boolean,
        isLastMessage: Boolean,
    ): RoundedCornerShape {
        return when {
            isFirstMessage && isLastMessage -> SINGLE_MESSAGE
            !isFirstMessage && !isLastMessage -> CENTER_MY_MESSAGE
            isFirstMessage -> BOTTOM_MY_MESSAGE
            else -> TOP_MY_MESSAGE
        }
    }
}

@Composable
private fun ChatItemBubble(
    conversationId: Long,
    message: Message,
    conversationType: ConversationType,
    isFirstMessageByAuthorSameDay: Boolean,
    isLastMessageByAuthorSameDay: Boolean,
    reactIcons: List<ReactIcon>,
    showReaction: Boolean,
    onMessageReply: (Message) -> Unit,
    handleEvent: (ChatEvent) -> Unit,
) {
    val isMe = message.sender.isMe
    var backgroundBubbleShape: Shape = MessageChatBubbleShape.getChatBubbleShape(
        isFirstMessageByAuthorSameDay,
        isLastMessageByAuthorSameDay,
        isMe
    )
    var backgroundBubbleColor: Color = MaterialTheme.colorScheme.surfaceVariant
    if (isMe) {
        backgroundBubbleColor = MaterialTheme.colorScheme.primaryContainer
    }
    when (message.type) {
        MessageType.DOCUMENT -> {
            backgroundBubbleShape = RoundedCornerShape(18.dp)
            if (message.sender.isMe) {
                backgroundBubbleColor = MaterialTheme.colorScheme.primaryContainer
            }
        }

        MessageType.GIF,
        MessageType.STICKER,
        MessageType.PHOTO,
        -> {
            backgroundBubbleShape = RectangleShape
            backgroundBubbleColor = Color.Transparent
        }

        else -> {}
    }
    val hasReaction = message.reactions.isNotEmpty() || !isMe

    val paddingBottom = if (hasReaction && showReaction) 15.dp else 0.dp
    val alignment = if (isMe) Alignment.BottomEnd else Alignment.BottomStart
    var selectMessage by remember {
        mutableStateOf<Message?>(null)
    }

    Box(contentAlignment = alignment) {
        Column(
            modifier = message.replyToMessage?.let {
                Modifier
                    .padding(bottom = paddingBottom)
                    .clip(backgroundBubbleShape)
                    .background(backgroundBubbleColor)
            } ?: Modifier.padding(bottom = paddingBottom)
        ) {
            message.replyToMessage?.let {
                PlaceholderReplyMessage(it, isMe = isMe)
            }
            Box {
                if (isLastMessageByAuthorSameDay && message.canShowArrow()) {
                    if (isMe) {
                        Icon(
                            painterResource(id = R.drawable.ic_chat_arrow_mine),
                            contentDescription = null,
                            modifier = Modifier
                                .align(
                                    Alignment.BottomEnd
                                )
                                .size(12.dp),
                            tint = backgroundBubbleColor
                        )
                    } else if (conversationType == ConversationType.SINGLE) {
                        Icon(
                            painterResource(id = R.drawable.ic_chat_arrow),
                            contentDescription = null,
                            modifier = Modifier
                                .align(
                                    Alignment.BottomStart
                                )
                                .size(12.dp),
                            tint = backgroundBubbleColor
                        )
                    }
                }

                Surface(
                    color = backgroundBubbleColor,
                    contentColor = MaterialTheme.colorScheme.background,
                    shape = backgroundBubbleShape,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(horizontal = 6.dp)
                ) {
                    ClickableMessage(
                        conversationId = conversationId,
                        message = message,
                        onMessageLongClick = {
                            selectMessage = message
                        },
                        handleEvent = handleEvent
                    )
                }
            }
        }

        MessageReactions(
            message = message,
            alignment = alignment,
            showReaction = showReaction
        )
        DefaultMessageReaction(
            message = message,
            alignment = alignment,
            showReaction = showReaction,
            reactIcons = reactIcons,
            onClick = { mesId, reactId ->
                handleEvent(
                    ChatEvent.React(
                        mesId,
                        reactId
                    )
                )
            }
        )
        ChatMessageMenuDialog(
            selectedMessage = selectMessage,
            conversationId = conversationId,
            conversationType = conversationType,
            reactIcons = reactIcons,
            onReact = { mesId, reactId ->
                handleEvent(
                    ChatEvent.React(
                        mesId,
                        reactId
                    )
                )
            },
            onReply = {
                onMessageReply.invoke(message)
            },
            onDelete = {
                handleEvent(
                    ChatEvent.DeleteMessage(
                        messageId = message.id,
                        removeForSelfOnly = false
                    )
                )
            },
            onShare = {
                handleEvent(
                    ChatEvent.SelectShareMessage(
                        message = message,
                    )
                )
            }
        ) {
            selectMessage = null
        }
    }
}

@Composable
private fun MessageEvent(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text,
            modifier = Modifier
                .padding(
                    start = 57.dp,
                    end = 57.dp,
                    bottom = 4.dp,
                    top = 3.dp,
                )
                .clip(RoundedCornerShape(7.dp))
                .background(BlueGray)
                .padding(vertical = 5.dp, horizontal = 10.dp),
            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.background),
            maxLines = 1,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun ScrollToNewestMessageButton(
    onClick: () -> Unit,
) {
    val borderRadius = RoundedCornerShape(
        10.dp,
        0.dp,
        0.dp,
        10.dp
    )
    Box(
        modifier = Modifier
            .padding(bottom = 30.dp)
            .width(53.dp)
            .height(35.dp)
            .clip(
                borderRadius
            )
            .background(color = MaterialTheme.colorScheme.primaryContainer)
            .shadow(elevation = 5.dp)
            .padding(start = 1.dp, bottom = 1.dp, top = 1.dp)
            .clickable {
                onClick.invoke()
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(borderRadius)
                .background(color = MaterialTheme.colorScheme.background)
                .padding(start = 9.dp)
        ) {
            Icon(
                Icons.Default.ExpandMore,
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp)
                    .align(Alignment.CenterStart)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                tint = MaterialTheme.colorScheme.background
            )
        }
    }
}

@Composable
private fun PlaceholderReplyMessage(message: Message, isMe: Boolean) {
    val contentColor = if (isMe) {
        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
    } else {
        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
    }
    Row(
        modifier = Modifier
            .height(IntrinsicSize.Min)
            .padding(horizontal = 5.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        VerticalDivider(
            thickness = 4.dp,
            color = contentColor,
            modifier = Modifier.clip(
                RoundedCornerShape(5.dp)
            )
        )

        when (message.type) {
            MessageType.PHOTO -> {
                Icon(
                    Icons.Default.Photo,
                    contentDescription = null,
                    modifier = Modifier.size(30.dp),
                    tint = contentColor
                )
                SizeBox(width = 5.dp)
            }

            MessageType.STICKER -> {
                SubcomposeAsyncImage(
                    ImageRequest.Builder(context = LocalContext.current)
                        .data("${Const.BASE_URL}/media/sticker-packs/${message.message}")
                        .memoryCacheKey(message.message)
                        .diskCacheKey(message.message)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp),
                )
                SizeBox(width = 5.dp)
            }

            else -> Unit
        }
        SizeBox(width = 5.dp)
        Column {
            val name = if (message.sender.isMe) {
                stringResource(id = R.string.common_you)
            } else {
                message.sender.name
            }
            Text(name, color = contentColor, fontSize = 12.sp)
            val description = when (message.type) {
                MessageType.PHOTO -> {
                    stringResource(id = R.string.common_image)
                }

                MessageType.STICKER -> {
                    stringResource(id = R.string.common_sticker)
                }

                else -> {
                    message.message.takeIf { it.isNotBlank() }
                        ?: stringResource(id = R.string.common_undefined)
                }
            }
            Text(
                text = description,
                color = contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun BoxScope.DefaultMessageReaction(
    message: Message,
    reactIcons: List<ReactIcon>,
    showReaction: Boolean,
    alignment: Alignment,
    onClick: (Long, Long) -> Unit,
) {
    var showListReactions by remember { mutableStateOf(false) }
    val reactTextSize = with(LocalDensity.current) {
        12.dp.toSp()
    }
    message.getDefaultReaction(reactIcons).takeIf { showReaction }?.let {
        Box(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .size(22.dp)
                .align(alignment)
                .clip(CircleShape)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.background,
                    shape = CircleShape
                )
                .background(
                    if (message.sender.isMe) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = it.code,
                style = TextStyle(
                    platformStyle = PlatformTextStyle(
                        emojiSupportMatch = EmojiSupportMatch.None
                    ),
                    lineHeight = reactTextSize,
                    fontSize = reactTextSize,
                    color = Color.Black
                ),
                modifier = Modifier.clicks(onClick = { _ ->
                    onClick(
                        message.id,
                        it.id
                    )
                }, onLongClick = {
                    showListReactions = true
                })
            )
            if (showListReactions) {
                ChatMessageReactionsDialog(
                    onDismissRequest = { showListReactions = false }
                ) {
                    ChatReactions(
                        reactions = reactIcons,
                        onReact = {
                            showListReactions = false
                            onClick(
                                message.id,
                                it
                            )
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun BoxScope.MessageReactions(
    message: Message,
    showReaction: Boolean,
    alignment: Alignment,
) {
    val reactTextSize = with(LocalDensity.current) {
        12.dp.toSp()
    }
    message.getDisplayReactions(showReaction)
        ?.let {
            Row(
                modifier = Modifier
                    .height(22.dp)
                    .align(alignment)
                    .padding(horizontal = 40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .background(
                        if (message.sender.isMe) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                    .padding(horizontal = 8.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                it.forEach {
                    Text(
                        text = it.reactionCode,
                        style = TextStyle(
                            platformStyle = PlatformTextStyle(
                                emojiSupportMatch = EmojiSupportMatch.None
                            ),
                            lineHeight = reactTextSize,
                            fontSize = reactTextSize,
                        ),
                    )
                }
                if (message.reactions.size != message.reactions.distinctBy { it.reactionCode }.size) {
                    SizeBox(width = 3.dp)
                    Text(
                        text = message.reactions.size.toString(),
                        style = TextStyle(
                            lineHeight = reactTextSize,
                            fontSize = reactTextSize,
                            color = if (message.sender.isMe) {
                                MaterialTheme.colorMyChatText()
                            } else {
                                MaterialTheme.colorScheme.onBackground
                            }
                        ),
                    )
                }
            }
        }
}