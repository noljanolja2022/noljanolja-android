package com.noljanolja.android.features.home.chat.components

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.EmojiSupportMatch
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import com.noljanolja.android.R
import com.noljanolja.android.features.home.chat.MessageRow
import com.noljanolja.android.ui.composable.SizeBox
import com.noljanolja.android.ui.theme.NeutralDarkGrey
import com.noljanolja.android.ui.theme.backgroundInPopup
import com.noljanolja.android.util.showToast
import com.noljanolja.core.conversation.domain.model.ConversationType
import com.noljanolja.core.conversation.domain.model.Message
import com.noljanolja.core.conversation.domain.model.ReactIcon

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ChatMessageMenuDialog(
    selectedMessage: Message?,
    conversationId: Long,
    conversationType: ConversationType,
    reactIcons: List<ReactIcon>,
    onReact: (Long, Long) -> Unit,
    onReply: () -> Unit,
    onDelete: () -> Unit,
    onShare: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val expandedStates = remember { MutableTransitionState(false) }
    expandedStates.targetState = true
    val transformOriginState = remember { mutableStateOf(TransformOrigin.Center) }
    val density = LocalDensity.current
    val popupPositionProvider = ChatMessageMenuPosition(
        DpOffset(0.dp, 0.dp),
        density,
        selectedMessage != null && (selectedMessage.reactions.isNotEmpty() || selectedMessage.sender.isMe)
    ) { parentBounds, menuBounds ->
        transformOriginState.value = calculateTransformOrigin(parentBounds, menuBounds)
    }
    val paddingStart = if (conversationType == ConversationType.GROUP) 60.dp else 22.dp
    if (selectedMessage != null) {
        val alignment = if (selectedMessage.sender.isMe) Alignment.End else Alignment.Start

        Popup(onDismissRequest = onDismissRequest) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = NeutralDarkGrey.copy(alpha = 0.5f)
            ) {
            }
        }
        Popup(
            onDismissRequest = onDismissRequest,
            popupPositionProvider = popupPositionProvider,
            properties = PopupProperties(
                focusable = true,
                usePlatformDefaultWidth = false,
            ),
        ) {
            Column(
                modifier = Modifier
                    .clickable { onDismissRequest.invoke() },
            ) {
                ChatReactions(
                    modifier = Modifier
                        .align(alignment)
                        .padding(end = 30.dp, start = paddingStart),
                    reactions = reactIcons,
                    onReact = {
                        onReact(selectedMessage.id, it)
                        onDismissRequest()
                    }
                )

                SizeBox(height = 3.dp)
                Box(
                    modifier = Modifier
                ) {
                    MessageRow(
                        conversationId = conversationId,
                        message = selectedMessage,
                        conversationType = conversationType,
                        isFirstMessageByAuthorSameDay = false,
                        isLastMessageByAuthorSameDay = false,
                        reactIcons = reactIcons,
                        showReaction = false,
                        handleEvent = {}
                    )
                }

                Row(
                    modifier = Modifier
                        .align(alignment)
                        .padding(end = 26.dp, start = paddingStart)
                        .clip(RoundedCornerShape(5.dp))
                        .background(MaterialTheme.backgroundInPopup())
                        .padding(start = 3.dp, end = 3.dp)
                        .height(65.dp),
                ) {
                    MessageAction(
                        title = stringResource(id = R.string.chat_action_reply),
                        icon = Icons.Default.Reply
                    ) {
                        onReply()
                        onDismissRequest()
                    }
                    MessageAction(
                        title = stringResource(id = R.string.chat_action_forward),
                        icon = ImageVector.vectorResource(id = R.drawable.ic_forward)
                    ) {
                        onShare()
                        onDismissRequest()
                    }
                    MessageAction(
                        title = stringResource(id = R.string.chat_action_copy),
                        icon = ImageVector.vectorResource(id = R.drawable.ic_copy)
                    ) {
                        clipboardManager.setText(AnnotatedString(selectedMessage.message))
                        context.showToast(context.getString(R.string.common_copy_success))
                        onDismissRequest()
                    }
                    MessageAction(
                        title = stringResource(id = R.string.chat_action_delete),
                        icon = Icons.Default.Delete
                    ) {
                        onDelete()
                        onDismissRequest()
                    }
                }
            }
        }
    }
}

@Composable
fun ChatReactions(
    reactions: List<ReactIcon>,
    onReact: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .height(30.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(MaterialTheme.backgroundInPopup())
            .padding(horizontal = 5.dp)
    ) {
        val density = LocalDensity.current
        val fontSize = with(density) { 20.dp.toSp() }
        reactions.forEach {
            Text(
                text = it.code,
                style = TextStyle(
                    platformStyle = PlatformTextStyle(
                        emojiSupportMatch = EmojiSupportMatch.None
                    ),
                    fontSize = fontSize,
                    lineHeight = fontSize
                ),
                modifier = Modifier
                    .clickable {
                        onReact(it.id)
                    }
                    .padding(horizontal = 5.dp)
            )
        }
    }
}

@Composable
private fun MessageAction(title: String, icon: ImageVector, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .height(65.dp)
            .clickable { onClick.invoke() }
            .padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Text(
            title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

private data class ChatMessageMenuPosition(
    val contentOffset: DpOffset,
    val density: Density,
    val showReaction: Boolean,
    val onPositionCalculated: (IntRect, IntRect) -> Unit = { _, _ -> },
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset {
        // The min margin above and below the menu, relative to the screen.
        val verticalMargin = with(density) { 48.dp.roundToPx() }
        // The content offset specified using the dropdown offset parameter.
        val contentOffsetX = with(density) { contentOffset.x.roundToPx() }
        val contentOffsetY = with(density) { contentOffset.y.roundToPx() }

        // Compute horizontal position.
        val toRight = anchorBounds.left + contentOffsetX
        val toLeft = anchorBounds.right - contentOffsetX - popupContentSize.width
        val toDisplayRight = windowSize.width - popupContentSize.width
        val toDisplayLeft = 0
        val x = if (layoutDirection == LayoutDirection.Ltr) {
            sequenceOf(
                toRight,
                toLeft,
                // If the anchor gets outside of the window on the left, we want to position
                // toDisplayLeft for proximity to the anchor. Otherwise, toDisplayRight.
                if (anchorBounds.left >= 0) toDisplayRight else toDisplayLeft
            )
        } else {
            sequenceOf(
                toLeft,
                toRight,
                // If the anchor gets outside of the window on the right, we want to position
                // toDisplayRight for proximity to the anchor. Otherwise, toDisplayLeft.
                if (anchorBounds.right <= windowSize.width) toDisplayLeft else toDisplayRight
            )
        }.firstOrNull {
            it >= 0 && it + popupContentSize.width <= windowSize.width
        } ?: toLeft

        val toBottom = maxOf(anchorBounds.bottom + contentOffsetY, verticalMargin)
        val toTop = anchorBounds.top - contentOffsetY - popupContentSize.height
        val toCenter = anchorBounds.top
        val toDisplayBottom = windowSize.height - popupContentSize.height - verticalMargin
        // Compute vertical position.
        val y = with(density) {
            toCenter - 33.dp.toPx().toInt()
        }

        onPositionCalculated(
            anchorBounds,
            IntRect(x, y, x + popupContentSize.width, y + popupContentSize.height)
        )
        return IntOffset(x, y)
    }
}
