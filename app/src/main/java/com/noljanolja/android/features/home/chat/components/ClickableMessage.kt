package com.noljanolja.android.features.home.chat.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SyncProblem
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.*
import coil.request.ImageRequest
import com.noljanolja.android.R
import com.noljanolja.android.ui.composable.CombineClickableText
import com.noljanolja.android.util.chatMessageBubbleTime
import com.noljanolja.android.util.openImageFromCache
import com.noljanolja.android.util.openUrl
import com.noljanolja.android.util.setAnimated
import com.noljanolja.android.util.showToast
import com.noljanolja.android.util.toUri
import com.noljanolja.core.conversation.domain.model.Message
import com.noljanolja.core.conversation.domain.model.MessageAttachment
import com.noljanolja.core.conversation.domain.model.MessageStatus
import com.noljanolja.core.conversation.domain.model.MessageType
import com.noljanolja.core.utils.Const

@Composable
fun ClickableMessage(
    conversationId: Long,
    message: Message,
    onMessageClick: (Message) -> Unit,
) {
    when (message.type) {
        MessageType.PLAINTEXT -> {
            ClickableTextMessage(
                message = message,
                modifier = Modifier
                    .clickable { onMessageClick(message) }
                    .padding(vertical = 5.dp, horizontal = 10.dp),
            )
        }

        MessageType.DOCUMENT, MessageType.PHOTO -> {
            ClickablePhotoMessage(
                message = message,
                conversationId = conversationId,
                modifier = Modifier,
                onMessageClick = onMessageClick
            )
        }

        MessageType.STICKER -> {
            ClickableStickerMessage(
                message = message,
                modifier = Modifier.clickable { onMessageClick(message) },
            )
        }

        else -> {
            Text(
                text = "Unsupported message type",
                style = MaterialTheme.typography.bodyLarge.copy(color = LocalContentColor.current),
                modifier = Modifier.padding(vertical = 16.dp, horizontal = 12.dp),
            )
        }
    }
}

@Composable
private fun ClickableTextMessage(
    message: Message,
    modifier: Modifier,
) {
    val context = LocalContext.current
    val clipboardManager: ClipboardManager = LocalClipboardManager.current

    var showMenu by remember { mutableStateOf(false) }
    val styledMessage = AnnotatedString.Builder().apply {
        append(
            messageFormatter(
                text = message.message,
                primary = message.sender.isMe
            )
        )
        withStyle(SpanStyle(color = Color.Transparent)) {
            append(" " + message.createdAt.chatMessageBubbleTime())
        }
    }.toAnnotatedString()
    Box {
        CombineClickableText(
            text = styledMessage,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = if (message.status == MessageStatus.FAILED) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onPrimary
                },
            ),
            modifier = modifier,
            onClick = {
                styledMessage.getStringAnnotations(start = it, end = it).firstOrNull()
                    ?.let { annotation ->
                        context.openUrl(annotation.item)
                    }
            },
            onLongClick = {
                showMenu = true
            }
        )
        Text(
            message.createdAt.chatMessageBubbleTime(),
            style = TextStyle(
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 5.dp, end = 10.dp)
        )
    }

    if (showMenu) {
        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.common_copy)) },
                onClick = {
                    clipboardManager.setText(AnnotatedString(message.message))
                    context.showToast(context.getString(R.string.common_copy_success))
                    showMenu = false
                }
            )
        }
    }
}

@Composable
private fun ClickableStickerMessage(
    message: Message,
    modifier: Modifier,
) {
    SubcomposeAsyncImage(
        ImageRequest.Builder(context = LocalContext.current)
            .setAnimated(true)
            .data("${Const.BASE_URL}/media/sticker-packs/${message.message}")
            .memoryCacheKey(message.message)
            .diskCacheKey(message.message)
            .build(),
        contentDescription = null,
        modifier = modifier.size(128.dp),
    ) {
        AsyncImageState(modifier = Modifier.size(128.dp), contentScale = ContentScale.FillBounds)
    }
}

@Composable
fun ClickablePhotoMessage(
    message: Message,
    conversationId: Long,
    modifier: Modifier,
    onMessageClick: (Message) -> Unit,
) {
    val backgroundColor = if (message.sender.isMe) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }
    Box(
        modifier = Modifier
            .wrapContentWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(backgroundColor)
            .padding(3.dp)
    ) {
        when (val size = message.attachments.size) {
            1 -> {
                val attachment = message.attachments.first()
                Column {
                    PhotoPreview(
                        modifier = modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { onMessageClick(message.copy(attachments = listOf(attachment))) },
                        uri = attachment.getPhotoUri(conversationId).toUri(),
                        key = "${message.localId}/${attachment.originalName}",
                        contentScale = ContentScale.FillWidth,
                        isMe = message.sender.isMe,
                        time = message.createdAt.chatMessageBubbleTime(),
                        timeAlign = TextAlign.End,
                    )
                }
            }

            else -> {
                val maxAttachmentPerRow = if (size == 3) 3 else 2
                val attachmentRows = if (size < 4) 1 else 2
//                    size / maxAttachmentPerRow + 1.takeIf { size % maxAttachmentPerRow > 0 }.orZero()
                Column(
                    modifier = modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
//                    horizontalAlignment = if (message.sender.isMe) Alignment.End else Alignment.Start
                ) {
                    repeat(attachmentRows) { row ->
                        if (row > 0) {
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        AttachmentRow(
                            message = message,
                            conversationId = conversationId,
                            attachments = message.attachments.filterIndexed { index, _ -> index >= row * maxAttachmentPerRow && index < (row + 1) * maxAttachmentPerRow },
                            maxAttachmentPerRow = maxAttachmentPerRow,
                            onMessageClick = onMessageClick,
                            notShowImages = (size - 3).takeIf { it > 0 && row > 0 }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AttachmentRow(
    modifier: Modifier = Modifier,
    message: Message,
    conversationId: Long,
    notShowImages: Int? = null,
    attachments: List<MessageAttachment>,
    maxAttachmentPerRow: Int,
    onMessageClick: (Message) -> Unit,
) {
    val isMe = message.sender.isMe
    CompositionLocalProvider(LocalLayoutDirection provides if (message.sender.isMe) LayoutDirection.Rtl else LayoutDirection.Ltr) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight(),
        ) {
            repeat(maxAttachmentPerRow) { index ->
                if (index > 0) {
                    Spacer(modifier = Modifier.width(4.dp))
                }
                val attachment = attachments.getOrNull(index)
                attachment?.let {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                    ) {
                        PhotoPreview(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onMessageClick(message.copy(attachments = listOf(attachment))) },
                            uri = attachment.getPhotoUri(conversationId).toUri(),
                            key = "${message.localId}/${attachment.originalName}",
                            isMe = message.sender.isMe,
                            time = message.createdAt.chatMessageBubbleTime(),
                            timeAlign = if (isMe) TextAlign.Start else TextAlign.End
                        )
                        if ((index == 1 && !isMe) || (index == 0 && isMe)) {
                            notShowImages?.let {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color.Black.copy(alpha = 0.7F)),
                                ) {
                                    Text(
                                        text = if (isMe) "$it +" else "+ $it",
                                        style = TextStyle(
                                            fontSize = 36.sp,
                                            color = MaterialTheme.colorScheme.background
                                        ),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                } ?: Spacer(modifier = Modifier.weight(1F))
            }
        }
    }
}

@Composable
private fun PhotoPreview(
    modifier: Modifier,
    uri: Uri,
    key: String,
    time: String,
    isMe: Boolean,
    timeAlign: TextAlign = TextAlign.End,
    contentScale: ContentScale = ContentScale.Crop,
) {
    val context = LocalContext.current
    var boxWidth by remember {
        mutableStateOf(0)
    }
    val density = LocalDensity.current
    Box(
        modifier = modifier.clickable {
            context.openImageFromCache(key)
        },
        contentAlignment = if (isMe) Alignment.BottomEnd else Alignment.BottomStart,
    ) {
        SubcomposeAsyncImage(
            ImageRequest.Builder(context = LocalContext.current)
                .data(uri)
                .memoryCacheKey(key)
                .diskCacheKey(key)
                .build(),
            contentDescription = null,
            contentScale = contentScale,
            modifier = Modifier.onSizeChanged { size ->
                boxWidth = size.width
            }
        ) {
            AsyncImageState(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1F),
                contentScale = contentScale,
            )
        }
        Text(
            time,
            modifier = Modifier
                .width(with(density) { boxWidth.toDp() })
                .height(50.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            Color.Black,
                        ),
                    )
                )
                .padding(top = 25.dp, end = 10.dp),
            textAlign = timeAlign
        )
    }
}

@Composable
private fun SubcomposeAsyncImageScope.AsyncImageState(
    modifier: Modifier,
    contentScale: ContentScale,
) {
    when (painter.state) {
        is AsyncImagePainter.State.Loading -> {
            Box(
                modifier = modifier
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(40.dp),
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }

        is AsyncImagePainter.State.Error -> {
            Box(
                modifier = modifier
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.SyncProblem,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.errorContainer
                )
            }
        }

        else -> {
            val aspectRatio = (painter.state as? AsyncImagePainter.State.Success)
                ?.painter
                ?.intrinsicSize
                ?.let { it.width / it.height }
                .takeIf { contentScale != ContentScale.Crop }
            SubcomposeAsyncImageContent(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .then(
                        aspectRatio?.let {
                            Modifier.aspectRatio(it)
                        } ?: Modifier
                    ),
                contentScale = contentScale
            )
        }
    }
}