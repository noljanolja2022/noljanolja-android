package com.noljanolja.android.features.home.conversations

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.noljanolja.android.MainActivity.Companion.getConversationId
import com.noljanolja.android.MainActivity.Companion.removeConversationId
import com.noljanolja.android.R
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.common.sharedpreference.SharedPreferenceHelper
import com.noljanolja.android.features.home.chat.components.NewChatDialog
import com.noljanolja.android.ui.composable.CommonTopAppBar
import com.noljanolja.android.ui.composable.EmptyPage
import com.noljanolja.android.ui.composable.ScaffoldWithUiState
import com.noljanolja.android.util.findActivity
import com.noljanolja.android.util.humanReadableDate
import com.noljanolja.core.conversation.domain.model.Conversation
import com.noljanolja.core.conversation.domain.model.ConversationType
import com.noljanolja.core.conversation.domain.model.Message
import com.noljanolja.core.conversation.domain.model.MessageType
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ConversationsScreen(
    viewModel: ConversationsViewModel = getViewModel(),
) {
    LocalContext.current.findActivity()?.intent?.let { intent ->
        val conversationId = intent.getConversationId().also { intent.removeConversationId() }
        LaunchedEffect(conversationId) {
            if (conversationId > 0) {
                viewModel.handleEvent(ConversationsEvent.OpenConversation(conversationId))
            }
        }
    }
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    ConversationsScreenContent(
        uiState = uiState,
        handleEvent = viewModel::handleEvent,
    )
}

@Composable
fun ConversationsScreenContent(
    uiState: UiState<List<Conversation>>,
    handleEvent: (ConversationsEvent) -> Unit,
) {
    val sharedPreferenceHelper: SharedPreferenceHelper = get()

    var showNewChatDialog by remember {
        mutableStateOf(false)
    }
    var showNewChatTooltip by remember {
        mutableStateOf(sharedPreferenceHelper.showNewChatDialog)
    }
    var newChatIconPositions by remember {
        mutableStateOf(Pair(0f, 0f))
    }

    ScaffoldWithUiState(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CommonTopAppBar(
                title = stringResource(R.string.chats_title),
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            Icons.Filled.PersonAdd,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    IconButton(onClick = { }) {
                        Icon(
                            Icons.Outlined.Search,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    IconButton(onClick = {
                        showNewChatDialog = true
                        showNewChatTooltip = false
                        sharedPreferenceHelper.showNewChatDialog = false
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.chat_add),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(bottom = 2.dp)
                                .size(21.dp)
                                .onGloballyPositioned { coordinates ->
                                    val x = coordinates.positionInRoot().x

                                    val y = coordinates.positionInRoot().y
                                    newChatIconPositions = Pair(x, y)
                                },
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    IconButton(onClick = { }) {
                        Icon(
                            Icons.Outlined.Settings,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(bottom = 2.dp)
                                .size(21.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        },
        uiState = uiState,
        showContentWithLoading = false,
        error = {}
    ) {
        if (uiState.data.isNullOrEmpty()) {
            EmptyPage(
                stringResource(id = R.string.conversation_empty),
                icon = {
                    Icon(
                        Icons.Outlined.Error,
                        contentDescription = null,
                        modifier = Modifier.size(38.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            )
        } else {
            ConversationList(uiState.data) { conversation ->
                handleEvent(
                    ConversationsEvent.OpenConversation(
                        conversation.id,
                    )
                )
            }
        }
    }

    NewChatDialog(
        visible = showNewChatDialog,
        onDismissRequest = { showNewChatDialog = false },
        onNewSingleChat = { handleEvent(ConversationsEvent.OpenContactPicker(it)) },
        onNewSecretChat = { handleEvent(ConversationsEvent.OpenContactPicker(it)) },
        onNewGroupChat = { handleEvent(ConversationsEvent.OpenContactPicker(it)) },
    )
    if (showNewChatTooltip) {
        NewChatTooltip(iconPosition = newChatIconPositions, onDismissRequest = { showNewChatTooltip = false })
    }
}

@Composable
fun ConversationList(
    conversations: List<Conversation>,
    onItemClick: (Conversation) -> Unit,
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(conversations, key = { it.id }) { conversation ->
            ConversationRow(conversation) { onItemClick(it) }
        }
    }
}

@Composable
fun ConversationRow(
    conversation: Conversation,
    onClick: (Conversation) -> Unit,
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .clickable { onClick(conversation) }
            .padding(vertical = 10.dp, horizontal = 16.dp),
    ) {
        val message = conversation.messages.firstOrNull() ?: Message(
            message = stringResource(
                id = R.string.conversation_create,
                conversation.creator.takeIf { !it.isMe }?.name
                    ?: stringResource(id = R.string.common_you)
            ),
            type = MessageType.PLAINTEXT
        ).apply { isSeenByMe = true }.takeIf { conversation.type == ConversationType.GROUP }
            ?: return
        Box(
            modifier = Modifier
                .padding(top = 6.dp)
                .size(40.dp),
            contentAlignment = Alignment.BottomEnd,
        ) {
            val avatarModifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(14.dp))
            conversation.getDisplayAvatarUrl()?.let { url ->
                AsyncImage(
                    ImageRequest.Builder(context = context)
                        .data(url)
                        .placeholder(R.drawable.placeholder_avatar)
                        .error(R.drawable.placeholder_avatar)
                        .fallback(R.drawable.placeholder_avatar)
                        .build(),
                    contentDescription = null,
                    modifier = avatarModifier,
                    contentScale = ContentScale.Crop,
                )
            } ?: Icon(
                Icons.Filled.Group,
                contentDescription = null,
                modifier = avatarModifier.background(MaterialTheme.colorScheme.primaryContainer).padding(8.dp)
            )
        }

        Column(
            modifier = Modifier
                .padding(start = 16.dp, end = 24.dp)
                .weight(1f)
        ) {
            Text(
                text = conversation.getDisplayTitle(),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            val formattedMessage = when (message.type) {
                MessageType.PLAINTEXT -> {
                    message.message
                }

                MessageType.STICKER -> {
                    if (message.sender.isMe) {
                        stringResource(R.string.chats_message_my_sticker)
                    } else {
                        stringResource(R.string.chats_message_sticker)
                    }
                }

                MessageType.DOCUMENT, MessageType.GIF -> {
                    if (message.sender.isMe) {
                        stringResource(R.string.chats_message_my_file)
                    } else {
                        stringResource(R.string.chats_message_file)
                    }
                }

                MessageType.PHOTO -> {
                    val attachment = message.attachments.last()
                    if (message.sender.isMe) {
                        if (attachment.type.startsWith("video")) {
                            stringResource(R.string.chats_message_my_video)
                        } else {
                            stringResource(R.string.chats_message_my_photo)
                        }
                    } else {
                        if (attachment.type.startsWith("video")) {
                            stringResource(R.string.chats_message_video)
                        } else {
                            stringResource(R.string.chats_message_photo)
                        }
                    }
                }

                MessageType.EVENT_UPDATED, MessageType.EVENT_LEFT, MessageType.EVENT_JOINED -> {
                    stringResource(id = R.string.conversation_has_changed)
                }

                else -> {
                    if (message.sender.isMe) {
                        stringResource(R.string.chats_message_my_unknown)
                    } else {
                        stringResource(R.string.chats_message_unknown)
                    }
                }
            }
            Text(
                text = formattedMessage,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Column(
            modifier = Modifier.padding(top = 14.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.End,
        ) {
            Text(
                text = message.createdAt.humanReadableDate(),
                modifier = Modifier.wrapContentSize(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            )
            if (!message.isSeenByMe) {
                Text(
                    "1",
                    modifier = Modifier
                        .padding(top = 7.dp)
                        .size(15.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.errorContainer),
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.background
                    )
                )
            }
        }
    }
}

@Composable
private fun NewChatTooltip(
    iconPosition: Pair<Float, Float>,
    onDismissRequest: () -> Unit,
) {
    val density = LocalDensity.current
    val xInDp = with(density) { iconPosition.first.toDp() }
    val yInDp = with(density) { iconPosition.second.toDp() + 24.dp }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        RightTriangle(
            modifier = Modifier.padding(
                top = yInDp + 6.dp,
                start = xInDp + 3.dp
            ),
            size = 10.dp
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(end = 30.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "Please tap this button to start a new chat",
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(vertical = 13.dp, horizontal = 10.dp),
            )
        }
    }
}

@Composable
fun RightTriangle(
    modifier: Modifier = Modifier,
    size: Dp,
) {
    val pixel = with(LocalDensity.current) { size.toPx() }
    val background = MaterialTheme.colorScheme.primary
    Canvas(modifier = modifier) {
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(pixel, 0f)
            lineTo(pixel, -pixel)
            close()
        }
        drawPath(path, background)
    }
}
