package com.noljanolja.android.features.home.chat

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.noljanolja.android.R
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.features.home.chat.components.ChatInput
import com.noljanolja.android.features.home.chat.components.ClickableMessage
import com.noljanolja.android.features.home.chat.components.GridContent
import com.noljanolja.android.ui.composable.CommonTopAppBar
import com.noljanolja.android.ui.composable.FullSizeWithBottomSheet
import com.noljanolja.android.ui.composable.InfiniteListHandler
import com.noljanolja.android.ui.composable.OvalAvatar
import com.noljanolja.android.ui.composable.ScaffoldWithUiState
import com.noljanolja.android.ui.composable.SizeBox
import com.noljanolja.android.ui.theme.BlueGray
import com.noljanolja.android.util.*
import com.noljanolja.core.conversation.domain.model.*
import com.noljanolja.core.media.domain.model.Sticker
import com.noljanolja.core.user.domain.model.User
import kotlinx.coroutines.flow.SharedFlow
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
    val isLeave = savedStateHandle.get<Boolean>("leave") ?: false
    LaunchedEffect(key1 = isLeave) {
        if (isLeave) {
            savedStateHandle.remove<Boolean>("leave")
            viewModel.handleEvent(ChatEvent.GoBack)
        }
    }
    LaunchedEffect(key1 = conversationId) {
        viewModel.handleEvent(ChatEvent.LoadMedia)
    }
    val chatUiState by viewModel.chatUiStateFlow.collectAsStateWithLifecycle()
    val loadedMedia by viewModel.loadedMediaFlow.collectAsStateWithLifecycle()
    ChatScreenContent(
        chatUiState = chatUiState,
        mediaList = loadedMedia,
        scrollToNewMessageEvent = viewModel.scrollToNewMessageEvent,
        handleEvent = viewModel::handleEvent,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ChatScreenContent(
    chatUiState: UiState<Conversation>,
    mediaList: List<Pair<Uri, Long?>>,
    scrollToNewMessageEvent: SharedFlow<Unit>,
    handleEvent: (ChatEvent) -> Unit,
) {
    val scrollState = rememberLazyListState()
    val firstItemVisible = remember { derivedStateOf { scrollState.firstVisibleItemIndex } }
    var stickerSelected by remember {
        mutableStateOf<Sticker?>(null)
    }
    LaunchedEffect(true) {
        scrollToNewMessageEvent.collect {
            scrollState.animateScrollToItem(0)
        }
    }

    val context = LocalContext.current

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val scope = rememberCoroutineScope()
    val conversation = chatUiState.data ?: return
    val selectedMedia = remember { mutableStateListOf<Uri>() }

    val bottomSheetState = rememberBottomSheetScaffoldState()

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
                Text(stringResource(id = R.string.common_all), style = MaterialTheme.typography.titleMedium)
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
                            OvalAvatar(user = it, size = 34.dp, modifier = Modifier.padding(end = 10.dp))
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
                            loadMoreMessages = { handleEvent(ChatEvent.LoadMoreMessages) },
                            navigateToProfile = { user ->
                                handleEvent(
                                    ChatEvent.NavigateToProfile(
                                        user
                                    )
                                )
                            },
                            modifier = Modifier.fillMaxSize(),
                            scrollState = scrollState,
                            onMessageClick = { handleEvent(ChatEvent.ClickMessage(it)) }
                        )
                        if (firstItemVisible.value != 0) {
                            ScrollToNewestMessageButton(onClick = {
                                scope.launch {
                                    scrollState.scrollToItem(0)
                                }
                            })
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
                            sendMessage?.let { handleEvent(ChatEvent.SendMessage(it)) }
                        },
                        modifier = Modifier
                            .navigationBarsPadding()
                            .imePadding(),
                        selectedMedia = selectedMedia,
                        resetScroll = { scope.launch { scrollState.scrollToItem(0) } },
                        mediaList = mediaList,
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
    loadMoreMessages: () -> Unit,
    navigateToProfile: (User) -> Unit,
    scrollState: LazyListState,
    modifier: Modifier = Modifier,
    onMessageClick: (Message) -> Unit,
) {
    val scope = rememberCoroutineScope()
    Box(modifier = modifier) {
        LazyColumn(
            reverseLayout = true,
            state = scrollState,
            modifier = Modifier.fillMaxSize()
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

                item(key = "${message.id} ${message.localId}") {
                    MessageRow(
                        conversationId = conversationId,
                        message = message,
                        conversationType = conversationType,
                        isFirstMessageByAuthorSameDay = isFirstMessageByAuthorSameDay,
                        isLastMessageByAuthorSameDay = isLastMessageByAuthorSameDay,
                        onMessageClick = onMessageClick,
                        onSenderClick = { user -> navigateToProfile(user) },
                    )
                }

                if (nextMessage == null) {
                    item {
                        DayHeader(message.createdAt.chatMessageHeaderDate(context = LocalContext.current))
                    }
                }
            }
        }
        InfiniteListHandler(scrollState, onLoadMore = loadMoreMessages)
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

@Preview
@Composable
private fun DayHeaderPreview() {
    DayHeader("Friday, 17 December, 2021")
}

@Composable
private fun MessageRow(
    conversationId: Long,
    message: Message,
    conversationType: ConversationType,
    isFirstMessageByAuthorSameDay: Boolean,
    isLastMessageByAuthorSameDay: Boolean,
    onMessageClick: (Message) -> Unit,
    onSenderClick: (User) -> Unit,
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
                                .clickable { onSenderClick(message.sender) }
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
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .weight(1f),
                    onMessageClick = onMessageClick,
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
    modifier: Modifier = Modifier,
    onMessageClick: (Message) -> Unit,
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
                    onMessageClick = onMessageClick,
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
    onMessageClick: (Message) -> Unit,
) {
    var backgroundBubbleShape: Shape = MessageChatBubbleShape.getChatBubbleShape(
        isFirstMessageByAuthorSameDay,
        isLastMessageByAuthorSameDay,
        message.sender.isMe
    )
    var backgroundBubbleColor: Color = MaterialTheme.colorScheme.surfaceVariant
    if (message.sender.isMe) {
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

    Box {
        if (isLastMessageByAuthorSameDay && (message.type == MessageType.PHOTO || message.type == MessageType.PLAINTEXT)) {
            if (message.sender.isMe) {
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
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(horizontal = 6.dp)
        ) {
            Surface(
                color = backgroundBubbleColor,
                contentColor = MaterialTheme.colorScheme.background,
                shape = backgroundBubbleShape,
            ) {
                ClickableMessage(
                    conversationId = conversationId,
                    message = message,
                    onMessageClick = onMessageClick,
                )
            }
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