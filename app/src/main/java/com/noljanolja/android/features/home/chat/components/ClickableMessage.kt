//package com.noljanolja.android.features.home.chat.components
//
//import android.net.Uri
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.text.ClickableText
//import androidx.compose.material.CircularProgressIndicator
//import androidx.compose.material3.Icon
//import androidx.compose.material3.LocalContentColor
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.unit.dp
//import coil.compose.AsyncImage
//import coil.compose.AsyncImageContent
//import coil.compose.AsyncImagePainter
//import coil.request.ImageRequest
//
//
//@Composable
//fun ClickableMessage(
//    conversationId: Long,
//    message: Message,
//    onMessageClick: (Message) -> Unit,
//) {
//    when (message.type) {
//        MessageType.PlainText -> {
//            ClickableTextMessage(
//                message = message,
//                modifier = Modifier.clickable { onMessageClick(message) }
//                    .padding(vertical = 16.dp, horizontal = 12.dp),
//            )
//        }
//        MessageType.Sticker -> {
//            ClickableStickerMessage(
//                message = message,
//                modifier = Modifier.clickable { onMessageClick(message) },
//            )
//        }
//        MessageType.Photo -> {
//            ClickablePhotoMessage(
//                message = message,
//                modifier = Modifier,
//                onMessageClick = onMessageClick,
//            )
//        }
//        MessageType.Document -> {
//            ClickableDocumentMessage(
//                message = message,
//                modifier = Modifier.clickable { onMessageClick(message) }
//                    .padding(vertical = 16.dp, horizontal = 12.dp),
//            )
//        }
//        MessageType.Gif -> {
//            ClickableGifMessage(
//                message = message,
//                modifier = Modifier,
//                onMessageClick = onMessageClick,
//            )
//        }
//        else -> {
//            Text(
//                text = "Unsupported message type",
//                style = MaterialTheme.typography.bodyLarge.copy(color = LocalContentColor.current),
//                modifier = Modifier.padding(vertical = 16.dp, horizontal = 12.dp),
//            )
//        }
//    }
//}
//
//@Composable
//private fun ClickableTextMessage(
//    message: Message,
//    modifier: Modifier,
//) {
//    val styledMessage = messageFormatter(
//        text = message.message,
//        primary = message.sender.isMe
//    )
//
//    ClickableText(
//        text = styledMessage,
//        style = MaterialTheme.typography.bodyMedium.copy(
//            color = if (message.sender.isMe) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
//        ),
//        modifier = modifier,
//        onClick = {
//            styledMessage.getStringAnnotations(start = it, end = it).firstOrNull()?.let { annotation ->
//                // TODO
//            }
//        }
//    )
//}
//
//@Composable
//private fun ClickableStickerMessage(
//    message: Message,
//    modifier: Modifier,
//) {
//    AsyncImage(
//        ImageRequest.Builder(context = LocalContext.current)
//            .data(message.stickerUrl)
//            .memoryCacheKey(message.message)
//            .diskCacheKey(message.message)
//            .build(),
//        contentDescription = null,
//        modifier = modifier.size(128.dp),
//    )
//}
//
//@Composable
//private fun ClickablePhotoMessage(
//    message: Message,
//    modifier: Modifier,
//    onMessageClick: (Message) -> Unit,
//) {
//    val size = message.attachments.size
//    if (size == 1) {
//        val attachment = message.attachments.first()
//        val photoUrl = attachment.localPath.takeIf { it.isNotBlank() } ?: attachment.url
//        Column(
//            modifier = modifier.fillMaxWidth().wrapContentHeight(),
//            horizontalAlignment = if (message.sender.isMe) Alignment.End else Alignment.Start
//        ) {
//            PhotoPreview(
//                modifier = modifier.fillMaxWidth()
//                    .wrapContentHeight()
//                    .clip(RoundedCornerShape(6.dp))
//                    .clickable { onMessageClick(message.copy(attachments = listOf(attachment))) },
//                uri = photoUrl.toUri(),
//                key = "${message.localId}/${attachment.originalName}",
//                isVideo = attachment.type.startsWith("video")
//            )
//        }
//    } else {
//        val maxAttachmentPerRow = 3
//        val attachmentRows = with(size / maxAttachmentPerRow) {
//            if (size % maxAttachmentPerRow == 0) this else this + 1
//        }
//        Column(
//            modifier = modifier.fillMaxWidth().wrapContentHeight(),
//            horizontalAlignment = if (message.sender.isMe) Alignment.End else Alignment.Start
//        ) {
//            repeat(attachmentRows) { x ->
//                Row(
//                    modifier = Modifier.fillMaxWidth().wrapContentHeight(),
//                    horizontalArrangement = Arrangement.SpaceEvenly,
//                ) {
//                    val attachmentPerRow = when {
//                        size - x * maxAttachmentPerRow > maxAttachmentPerRow -> maxAttachmentPerRow
//                        else -> size - x * maxAttachmentPerRow
//                    }
//                    repeat(attachmentPerRow) { y ->
//                        val attachment = message.attachments[x * maxAttachmentPerRow + y]
//                        val photoUrl = attachment.localPath.takeIf { it.isNotBlank() } ?: attachment.url
//                        PhotoPreview(
//                            modifier = modifier.weight(1f)
//                                .aspectRatio(1f)
//                                .clip(RoundedCornerShape(6.dp))
//                                .clickable { onMessageClick(message.copy(attachments = listOf(attachment))) },
//                            uri = photoUrl.toUri(),
//                            key = "${message.localId}/${attachment.originalName}",
//                            isVideo = attachment.type.startsWith("video")
//                        )
//                        if (y < attachmentPerRow) {
//                            Spacer(modifier = Modifier.width(2.dp))
//                        }
//                    }
//                }
//                if (x < attachmentRows) {
//                    Spacer(modifier = Modifier.height(2.dp))
//                }
//            }
//        }
//    }
//}
//
//@Composable
//private fun PhotoPreview(
//    modifier: Modifier,
//    uri: Uri,
//    key: String,
//    isVideo: Boolean,
//) {
//    Box(
//        modifier = modifier,
//        contentAlignment = Alignment.Center,
//    ) {
//        Box(
//            contentAlignment = Alignment.BottomEnd,
//        ) {
//            AsyncImage(
//                ImageRequest.Builder(context = LocalContext.current)
//                    .data(uri)
//                    .memoryCacheKey(key)
//                    .diskCacheKey(key)
//                    .build(),
//                contentDescription = null,
//                contentScale = ContentScale.Crop,
//                modifier = Modifier.fillMaxSize(),
//            ) { state ->
//                if (state is AsyncImagePainter.State.Loading && state.painter == null) {
//                    CircularProgressIndicator()
//                } else {
//                    AsyncImageContent()
//                }
//            }
//            if (isVideo) {
//                Text(
//                    "00:00",
//                    style = MaterialTheme.typography.labelMedium.copy(
//                        color = Color.White,
//                    ),
//                    modifier = Modifier
//                        .padding(8.dp)
//                        .background(Color.Black.copy(alpha = 0.38f), CircleShape)
//                        .padding(horizontal = 6.dp, vertical = 2.dp)
//                )
//            }
//        }
//        if (isVideo) {
//            Image(painterResource(R.drawable.ic_video_play_fill), contentDescription = null)
//        }
//    }
//}
//
//@Composable
//private fun ClickableDocumentMessage(
//    message: Message,
//    modifier: Modifier,
//) {
//    val attachment = message.attachments.first()
//    Row(modifier = modifier) {
//        if (message.sender.isMe) {
//            Icon(
//                painterResource(R.drawable.ic_file_line),
//                contentDescription = null,
//                tint = MaterialTheme.colorScheme.onPrimaryContainer,
//            )
//            Spacer(modifier = Modifier.width(16.dp))
//            DocumentDescription(attachment)
//        } else {
//            DocumentDescription(attachment)
//            Spacer(modifier = Modifier.width(16.dp))
//            Icon(
//                painterResource(R.drawable.ic_file_line),
//                contentDescription = null,
//                tint = MaterialTheme.colorScheme.onPrimaryContainer,
//            )
//        }
//    }
//}
//
//@Composable
//private fun DocumentDescription(
//    attachment: MessageAttachment,
//) {
//    var size = attachment.size
//    var uom = "B"
//    if (size >= 1024) {
//        size /= 1024
//        uom = "KB"
//    }
//    if (size >= 1024) {
//        size /= 1024
//        uom = "MB"
//    }
//    Column {
//        Text(
//            attachment.originalName,
//            style = MaterialTheme.typography.titleMedium.copy(
//                color = MaterialTheme.colorScheme.onPrimaryContainer
//            )
//        )
//        Spacer(modifier = Modifier.height(4.dp))
//        Text(
//            stringResource(R.string.common_size, "$size $uom"),
//            style = MaterialTheme.typography.bodySmall.copy(
//                color = MaterialTheme.colorScheme.onPrimaryContainer
//            )
//        )
//    }
//}
//
//@Composable
//private fun ClickableGifMessage(
//    message: Message,
//    modifier: Modifier,
//    onMessageClick: (Message) -> Unit,
//) {
//    // 0.7f below is approximately a half width of screen width
//    AsyncImage(
//        message.message,
//        contentDescription = null,
//        contentScale = ContentScale.FillWidth,
//        modifier = modifier.fillMaxWidth(0.7f).wrapContentHeight()
//            .clip(RoundedCornerShape(18.dp))
//            .clickable { onMessageClick(message) },
//    )
//}