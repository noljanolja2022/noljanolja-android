package com.noljanolja.android.features.home.chat.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.SyncProblem
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.noljanolja.android.util.orZero
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
                    .padding(vertical = 16.dp, horizontal = 12.dp),
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
    val styledMessage = messageFormatter(
        text = message.message,
        primary = message.sender.isMe
    )

    ClickableText(
        text = styledMessage,
        style = MaterialTheme.typography.bodyMedium.copy(
            color = if (message.status == MessageStatus.FAILED) {
                MaterialTheme.colorScheme.error
            } else if (message.sender.isMe) {
                MaterialTheme.colorScheme.background
            } else {
                MaterialTheme.colorScheme.onPrimary
            },
        ),
        modifier = modifier,
        onClick = {
            styledMessage.getStringAnnotations(start = it, end = it).firstOrNull()
                ?.let { annotation ->
                    // TODO
                }
        }
    )
}

@Composable
private fun ClickableStickerMessage(
    message: Message,
    modifier: Modifier,
) {
    AsyncImage(
        ImageRequest.Builder(context = LocalContext.current)
            .data("${Const.BASE_URL}/media/sticker-packs/${message.message}")
            .memoryCacheKey(message.message)
            .diskCacheKey(message.message)
            .build(),
        contentDescription = null,
        modifier = modifier.size(128.dp),
    )
}

@Composable
fun ClickablePhotoMessage(
    message: Message,
    conversationId: Long,
    modifier: Modifier,
    onMessageClick: (Message) -> Unit,
) {
    when (val size = message.attachments.size) {
        1 -> {
            val attachment = message.attachments.first()
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                horizontalAlignment = if (message.sender.isMe) Alignment.End else Alignment.Start
            ) {
                PhotoPreview(
                    modifier = modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { onMessageClick(message.copy(attachments = listOf(attachment))) },
                    uri = attachment.getPhotoUri(conversationId).toUri(),
                    key = "${message.localId}/${attachment.originalName}",
                    contentScale = ContentScale.FillWidth,
                )
            }
        }
        else -> {
            val maxAttachmentPerRow = if (size == 2 || size == 4) 2 else 3
            val attachmentRows =
                size / maxAttachmentPerRow + 1.takeIf { size % maxAttachmentPerRow > 0 }.orZero()
            Column(
                modifier = modifier.fillMaxWidth().wrapContentHeight(),
                horizontalAlignment = if (message.sender.isMe) Alignment.End else Alignment.Start
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
                        onMessageClick = onMessageClick
                    )
                }
            }
        }
    }
}

@Composable
fun AttachmentRow(
    message: Message,
    conversationId: Long,
    attachments: List<MessageAttachment>,
    maxAttachmentPerRow: Int,
    onMessageClick: (Message) -> Unit,
) {
    CompositionLocalProvider(LocalLayoutDirection provides if (message.sender.isMe) LayoutDirection.Rtl else LayoutDirection.Ltr) {
        Row(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            horizontalArrangement = if (message.sender.isMe) Arrangement.End else Arrangement.Start,
        ) {
            repeat(maxAttachmentPerRow) { index ->
                if (index > 0) {
                    Spacer(modifier = Modifier.width(4.dp))
                }
                val attachment = attachments.getOrNull(index)
                attachment?.let {
                    PhotoPreview(
                        modifier = Modifier.weight(1f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { onMessageClick(message.copy(attachments = listOf(attachment))) },
                        uri = attachment.getPhotoUri(conversationId).toUri(),
                        key = "${message.localId}/${attachment.originalName}",
                    )
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
    contentScale: ContentScale = ContentScale.Crop,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        SubcomposeAsyncImage(
            ImageRequest.Builder(context = LocalContext.current)
                .data(uri)
                .memoryCacheKey(key)
                .diskCacheKey(key)
                .build(),
            contentDescription = null,
            contentScale = contentScale,
        ) {
            when (painter.state) {
                is AsyncImagePainter.State.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .aspectRatio(1f)
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
                        modifier = Modifier.fillMaxWidth()
                            .aspectRatio(1f)
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
                    SubcomposeAsyncImageContent()
                }
            }
        }
    }
}