package com.noljanolja.android.features.home.conversations

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.noljanolja.android.R
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.common.conversation.domain.model.Conversation
import com.noljanolja.android.common.conversation.domain.model.MessageType
import com.noljanolja.android.ui.composable.CommonTopAppBar
import com.noljanolja.android.ui.composable.EmptyPage
import com.noljanolja.android.ui.composable.ScaffoldWithUiState
import com.noljanolja.android.util.humanReadableDate

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ConversationsScreen(
    viewModel: ConversationsViewModel = hiltViewModel(),
) {
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
    ScaffoldWithUiState(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CommonTopAppBar(title = stringResource(R.string.chats_title))
        },
        floatingActionButton = {
            NewChatButton(
                hasConversation = !uiState.data.isNullOrEmpty(),
                onItemClick = {
                    handleEvent(ConversationsEvent.OpenContactPicker)
                }
            )
        },
        uiState = uiState,
        error = {}
    ) {
        if (uiState.data.isNullOrEmpty()) {
            EmptyPage("No conversations found")
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
}

@Composable
fun NewChatButton(
    hasConversation: Boolean,
    onItemClick: () -> Unit,
) {
    if (hasConversation) {
        FloatingActionButton(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            content = {
                Icon(
                    Icons.Outlined.Chat,
                    contentDescription = null,
                )
            },
            onClick = onItemClick
        )
    } else {
        ExtendedFloatingActionButton(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            icon = {
                Icon(
                    Icons.Outlined.Chat,
                    contentDescription = null,
                )
            },
            text = {
                Text(
                    stringResource(R.string.chats_new_chat),
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                    )
                )
            },
            onClick = onItemClick,
        )
    }
}

@Composable
fun ConversationList(
    conversations: List<Conversation>,
    onItemClick: (Conversation) -> Unit,
) {
    LazyColumn {
        items(conversations) { conversation ->
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
        val message = conversation.messages.first()

        Box(
            modifier = Modifier
                .padding(top = 6.dp)
                .size(36.dp),
            contentAlignment = Alignment.BottomEnd,
        ) {
            AsyncImage(
                ImageRequest.Builder(context = context)
                    .data(conversation.getDisplayAvatarUrl())
                    .placeholder(R.drawable.placeholder_avatar)
                    .error(R.drawable.placeholder_avatar)
                    .fallback(R.drawable.placeholder_avatar)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop,
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
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            val formattedMessage = when (message.type) {
                MessageType.PlainText -> {
                    message.message
                }
                MessageType.Sticker -> {
                    if (message.sender.isMe) {
                        stringResource(R.string.chats_message_my_sticker)
                    } else {
                        stringResource(R.string.chats_message_sticker)
                    }
                }
                MessageType.Document, MessageType.Gif -> {
                    if (message.sender.isMe) {
                        stringResource(R.string.chats_message_my_file)
                    } else {
                        stringResource(R.string.chats_message_file)
                    }
                }
//                MessageType.Photo -> {
//                    val attachment = message.attachments.last()
//                    if (message.sender.isMe) {
//                        if (attachment.type.startsWith("video")) {
//                            stringResource(R.string.chats_message_my_video)
//                        } else {
//                            stringResource(R.string.chats_message_my_photo)
//                        }
//                    } else {
//                        if (attachment.type.startsWith("video")) {
//                            stringResource(R.string.chats_message_video)
//                        } else {
//                            stringResource(R.string.chats_message_photo)
//                        }
//                    }
//                }
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
        }
    }
}