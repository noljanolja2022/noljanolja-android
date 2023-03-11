package com.noljanolja.android.features.home.chat

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.noljanolja.android.R
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.features.home.chat.components.ChatInput
import com.noljanolja.android.features.home.chat.components.ClickableMessage
import com.noljanolja.android.ui.composable.CommonTopAppBar
import com.noljanolja.android.ui.composable.InfiniteListHandler
import com.noljanolja.android.ui.composable.ScaffoldWithUiState
import com.noljanolja.android.util.chatMessageBubbleTime
import com.noljanolja.android.util.chatMessageHeaderDate
import com.noljanolja.android.util.isSameDate
import com.noljanolja.core.conversation.domain.model.Conversation
import com.noljanolja.core.conversation.domain.model.Message
import com.noljanolja.core.conversation.domain.model.MessageType
import com.noljanolja.core.user.domain.model.User
import kotlinx.coroutines.launch

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ChatScreen(
    conversationId: Long,
    userId: String,
    userName: String,
    viewModel: ChatViewModel = hiltViewModel(),
) {
    LaunchedEffect(key1 = conversationId) {
        viewModel.setupConversation(
            conversationId = conversationId,
            userId = userId,
            userName = userName
        )
    }
    val chatUiState by viewModel.chatUiStateFlow.collectAsStateWithLifecycle()
    ChatScreenContent(
        chatUiState = chatUiState,
        handleEvent = viewModel::handleEvent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreenContent(
    chatUiState: UiState<Conversation>,
    handleEvent: (ChatEvent) -> Unit,
) {
    val scrollState = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val scope = rememberCoroutineScope()
    val conversation = chatUiState.data ?: return

    ScaffoldWithUiState(
        uiState = chatUiState,
        topBar = {
            CommonTopAppBar(
                title = conversation.getDisplayTitle(),
                onBack = { handleEvent(ChatEvent.GoBack) }
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
                    contentAlignment = Alignment.BottomCenter,
                ) {
                    MessageList(
                        conversationId = conversation.id,
                        messages = conversation.messages,
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
                            else -> null
                        }
                        sendMessage?.let { handleEvent(ChatEvent.SendMessage(it)) }
                    },
                    modifier = Modifier.navigationBarsPadding().imePadding(),
                    resetScroll = { scope.launch { scrollState.scrollToItem(0) } },
                )
            }
        }
    )
}

@Composable
private fun MessageList(
    conversationId: Long,
    messages: List<Message>,
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

                item {
                    MessageRow(
                        conversationId = conversationId,
                        message = message,
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
                .padding(vertical = 16.dp)
                .background(
                    MaterialTheme.colorScheme.surface.copy(alpha = 0.38F),
                    shape = CircleShape
                )
        ) {
            Text(
                text = dayString,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.87f),
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
    isFirstMessageByAuthorSameDay: Boolean,
    isLastMessageByAuthorSameDay: Boolean,
    onMessageClick: (Message) -> Unit,
    onSenderClick: (User) -> Unit,
) {
    val context = LocalContext.current
    val spaceBetweenAuthors =
        if (isLastMessageByAuthorSameDay) Modifier.padding(top = 8.dp) else Modifier

    Row(modifier = spaceBetweenAuthors) {
        if (isLastMessageByAuthorSameDay && !message.sender.isMe) {
            // Avatar
            Box(
                modifier = Modifier
                    .padding(start = 8.dp, end = 4.dp)
                    .size(32.dp),
                contentAlignment = Alignment.BottomEnd,
            ) {
                AsyncImage(
                    ImageRequest.Builder(context = context)
                        .data(message.sender.getAvatarUrl())
                        .placeholder(R.drawable.placeholder_avatar)
                        .error(R.drawable.placeholder_avatar)
                        .fallback(R.drawable.placeholder_avatar)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(13.dp))
                        .clickable { onSenderClick(message.sender) },
                    contentScale = ContentScale.Crop,
                )
            }
        } else {
            // Space under avatar
            Spacer(modifier = Modifier.width(44.dp))
        }
        AuthorAndTextMessage(
            conversationId = conversationId,
            message = message,
            isFirstMessageByAuthorSameDay = isFirstMessageByAuthorSameDay,
            isLastMessageByAuthorSameDay = isLastMessageByAuthorSameDay,
            modifier = Modifier
                .padding(end = 16.dp)
                .weight(1f),
            onMessageClick = onMessageClick,
        )
    }
}

@Composable
private fun AuthorAndTextMessage(
    conversationId: Long,
    message: Message,
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
        val maxChatItemWidth = (configuration.screenWidthDp * 0.7).dp
        val maxChatItemHeight = (configuration.screenHeightDp * 0.5).dp
        if (isLastMessageByAuthorSameDay && !message.sender.isMe) {
            Spacer(modifier = Modifier.height(12.dp))
        }
        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            if (message.sender.isMe) {
                Spacer(modifier = Modifier.weight(1F))
                MessageTimestamp(timestamp = message.createdAt.chatMessageBubbleTime())
            }

            Box(
                modifier = Modifier
                    .widthIn(0.dp, maxChatItemWidth)
                    .heightIn(0.dp, maxChatItemHeight)
            ) {
                ChatItemBubble(
                    conversationId = conversationId,
                    message = message,
                    isFirstMessageByAuthorSameDay = isFirstMessageByAuthorSameDay,
                    isLastMessageByAuthorSameDay = isLastMessageByAuthorSameDay,
                    onMessageClick = onMessageClick,
                )
            }
            if (!message.sender.isMe) {
                MessageTimestamp(timestamp = message.createdAt.chatMessageBubbleTime())
                Spacer(modifier = Modifier.weight(1F))
            }
        }
        if (isFirstMessageByAuthorSameDay) {
            Spacer(modifier = Modifier.height(12.dp))
        } else {
            Spacer(modifier = Modifier.height(4.dp))
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
    private val TOP_MESSAGE = RoundedCornerShape(18.dp, 18.dp, 18.dp, 6.dp)
    private val CENTER_MESSAGE = RoundedCornerShape(6.dp, 18.dp, 18.dp, 6.dp)
    private val BOTTOM_MESSAGE = RoundedCornerShape(6.dp, 18.dp, 18.dp, 18.dp)

    private val SINGLE_MESSAGE = RoundedCornerShape(18.dp)

    private val TOP_MY_MESSAGE = RoundedCornerShape(18.dp, 18.dp, 6.dp, 18.dp)
    private val CENTER_MY_MESSAGE = RoundedCornerShape(18.dp, 6.dp, 6.dp, 18.dp)
    private val BOTTOM_MY_MESSAGE = RoundedCornerShape(18.dp, 6.dp, 18.dp, 18.dp)

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
    isFirstMessageByAuthorSameDay: Boolean,
    isLastMessageByAuthorSameDay: Boolean,
    onMessageClick: (Message) -> Unit,
) {
    var backgroundBubbleShape: Shape = MessageChatBubbleShape.getChatBubbleShape(
        isFirstMessageByAuthorSameDay,
        isLastMessageByAuthorSameDay,
        message.sender.isMe
    )
    var backgroundBubbleColor: Color = MaterialTheme.colorScheme.primaryContainer
    if (message.sender.isMe) {
        backgroundBubbleColor = MaterialTheme.colorScheme.onPrimary
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

    Column {
        Surface(
            color = backgroundBubbleColor,
            contentColor = Color.White,
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