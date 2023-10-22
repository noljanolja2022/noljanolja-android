package com.noljanolja.android.features.home.chat.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SyncProblem
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.compose.SubcomposeAsyncImageScope
import coil.request.ImageRequest
import com.noljanolja.android.common.Const.VIDEO_IMAGE_RATIO
import com.noljanolja.android.features.home.chat.ChatEvent
import com.noljanolja.android.ui.composable.CombineClickableText
import com.noljanolja.android.ui.composable.SizeBox
import com.noljanolja.android.ui.theme.NeutralLight
import com.noljanolja.android.ui.theme.colorMyChatText
import com.noljanolja.android.util.chatMessageBubbleTime
import com.noljanolja.android.util.clicks
import com.noljanolja.android.util.openUrl
import com.noljanolja.android.util.setAnimated
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
    onMessageLongClick: (Message) -> Unit,
    handleEvent: (ChatEvent) -> Unit,
) {
    message.shareVideo?.let { video ->
        ClickableVideoMessage(
            message = message,
            conversationId = conversationId,
            modifier = Modifier.clicks(
                onLongClick = {
                    onMessageLongClick.invoke(message)
                },
                onClick = {
                    handleEvent(ChatEvent.OpenVideo(video.id))
                }
            ),
        )
    } ?: when (message.type) {
        MessageType.PLAINTEXT -> {
            ClickableTextMessage(
                message = message,
                modifier = Modifier
                    .padding(vertical = 5.dp, horizontal = 10.dp),
                onMessageLongClick = onMessageLongClick,
            )
        }

        MessageType.DOCUMENT, MessageType.PHOTO -> {
            ClickablePhotoMessage(
                message = message,
                conversationId = conversationId,
                modifier = Modifier,
                onMessageLongClick = onMessageLongClick,
                onMessageClick = {
                    handleEvent(
                        ChatEvent.ViewImages(
                            message.attachments.map { "${message.localId}/${it.originalName}" }

//                                    message.attachments.map { it.getAttachmentUrl(conversationId) }
                        )
                    )
                }
            )
        }

        MessageType.STICKER -> {
            ClickableStickerMessage(
                message = message,
                modifier = Modifier.clicks(
                    onLongClick = {
                        onMessageLongClick.invoke(message)
                    }
                ),
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
    onMessageLongClick: (Message) -> Unit,
) {
    val context = LocalContext.current
    val isMe = message.sender.isMe
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
                } else if (isMe) {
                    MaterialTheme.colorMyChatText()
                } else {
                    MaterialTheme.colorScheme.onBackground
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
                onMessageLongClick.invoke(message)
            }
        )
        Text(
            message.createdAt.chatMessageBubbleTime(),
            style = TextStyle(
                fontSize = 12.sp,
                color = if (isMe) {
                    MaterialTheme.colorMyChatText()
                } else {
                    MaterialTheme.colorScheme.onBackground
                }.copy(alpha = 0.6f)
            ),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 5.dp, end = 10.dp)
        )
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
            .data("${Const.BASE_URL}/api/v1/media/sticker-packs/${message.message}")
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
    onMessageLongClick: (Message) -> Unit,
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
                            .clip(RoundedCornerShape(6.dp)),
                        uri = attachment.getPhotoUri(conversationId).toUri(),
                        key = "${message.localId}/${attachment.originalName}",
                        contentScale = ContentScale.FillWidth,
                        isMe = message.sender.isMe,
                        time = message.createdAt.chatMessageBubbleTime(),
                        timeAlign = TextAlign.End,
                        onLongClick = {
                            onMessageLongClick.invoke(message)
                        },
                        onClick = {
                            onMessageClick.invoke(message)
                        }
                    )
                }
            }

            else -> {
                val maxAttachmentPerRow = if (size == 3) 3 else 2
                val attachmentRows = if (size < 4) 1 else 2
                Column(
                    modifier = modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
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
                            notShowImages = (size - 4).takeIf { it > 0 && row > 0 },
                            onLongClick = {
                                onMessageLongClick.invoke(message)
                            },
                            onClick = {
                                onMessageClick.invoke(message)
                            }
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
    onClick: () -> Unit,
    onLongClick: () -> Unit,
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
                                .clip(RoundedCornerShape(10.dp)),
                            uri = attachment.getPhotoUri(conversationId).toUri(),
                            key = "${message.localId}/${attachment.originalName}",
                            isMe = message.sender.isMe,
                            time = message.createdAt.chatMessageBubbleTime(),
                            timeAlign = if (isMe) TextAlign.Start else TextAlign.End,
                            onLongClick = onLongClick,
                            onClick = onClick
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
                                            color = NeutralLight
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
    onLongClick: () -> Unit,
    onClick: () -> Unit,
) {
    val context = LocalContext.current
    var boxWidth by remember {
        mutableStateOf(0)
    }
    val density = LocalDensity.current
    Box(
        modifier = modifier.clicks(
            onClick = {
                onClick()
            },
            onLongClick = onLongClick
        ),
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
            textAlign = timeAlign,
            style = MaterialTheme.typography.bodySmall.copy(NeutralLight)
        )
    }
}

@Composable
fun SubcomposeAsyncImageScope.AsyncImageState(
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

@Composable
fun ClickableVideoMessage(
    message: Message,
    conversationId: Long,
    modifier: Modifier = Modifier,
) {
    val video = message.shareVideo
    val configuration = LocalConfiguration.current
    Column(
        modifier = modifier
            .width((configuration.screenWidthDp / 2).dp)
            .padding(3.dp)
            .height(IntrinsicSize.Min)
    ) {
        SubcomposeAsyncImage(
            ImageRequest.Builder(context = LocalContext.current)
                .data(video?.thumbnail)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.weight(1f)
                .aspectRatio(VIDEO_IMAGE_RATIO)
        )
        SizeBox(height = 10.dp)
        Text(
            video?.title.orEmpty(),
            style = TextStyle(
                fontSize = 7.25.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )
    }
}