package com.noljanolja.android.features.home.chat.components

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.noljanolja.android.R
import com.noljanolja.android.features.home.chat.MessageRow
import com.noljanolja.android.ui.composable.SizeBox
import com.noljanolja.android.ui.theme.NeutralDarkGrey
import com.noljanolja.android.util.showToast
import com.noljanolja.core.conversation.domain.model.ConversationType
import com.noljanolja.core.conversation.domain.model.Message
import com.noljanolja.core.conversation.domain.model.ReactIcon
import com.noljanolja.core.utils.takeOrDefault
import org.koin.core.context.GlobalContext.get

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ChatMessageMenu(
    bottomPosition: Float,
    selectedMessage: Message?,
    conversationId: Long,
    conversationType: ConversationType,
    isFirstMessageByAuthorSameDay: Boolean,
    isLastMessageByAuthorSameDay: Boolean,
    reactIcons: List<ReactIcon>,
    onReact: (Long, Long) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val density = LocalDensity.current
    val bottomInDp = with(density) {
        bottomPosition.toDp() - 65.dp + 10.dp.takeOrDefault(0.dp) { isFirstMessageByAuthorSameDay } + 15.dp.takeOrDefault(
            0.dp
        ) { !selectedMessage?.reactions.isNullOrEmpty() }
    }.takeIf { it > 0.dp } ?: 0.dp
    val paddingStart = if (conversationType == ConversationType.GROUP) 60.dp else 22.dp
    if (selectedMessage != null) {
        val alignment = if (selectedMessage.sender.isMe) Alignment.End else Alignment.Start

        Popup(
            onDismissRequest = onDismissRequest,
            properties = PopupProperties(
                focusable = true,
                usePlatformDefaultWidth = false,
            ),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onDismissRequest() }
                    .background(NeutralDarkGrey.copy(alpha = 0.7f)),
                contentAlignment = Alignment.BottomCenter
            ) {
                Column(
                    modifier = Modifier
                        .clickable { onDismissRequest.invoke() }
                        .padding(bottom = bottomInDp)
                ) {
                    Row(
                        modifier = Modifier
                            .align(alignment)
                            .padding(end = 30.dp, start = paddingStart)
                            .clip(RoundedCornerShape(5.dp))
                            .background(MaterialTheme.colorScheme.background)
                            .padding(horizontal = 5.dp)
                    ) {
                        reactIcons.forEach {
                            Text(
                                text = it.code,
                                style = TextStyle(
                                    platformStyle = PlatformTextStyle(
                                        emojiSupportMatch = EmojiSupportMatch.None
                                    ),
                                    fontSize = 20.sp
                                ),
                                modifier = Modifier
                                    .clickable {
                                        onReact(selectedMessage.id, it.id)
                                        onDismissRequest()
                                    }
                                    .padding(5.dp)
                            )
                        }
                    }
                    SizeBox(height = 3.dp)
                    Box(
                        modifier = Modifier
                    ) {
                        MessageRow(
                            conversationId = conversationId,
                            message = selectedMessage.copy(reactions = emptyList()),
                            conversationType = conversationType,
                            isFirstMessageByAuthorSameDay = false,
                            isLastMessageByAuthorSameDay = false,
                            onSenderClick = { user ->
                            },
                            onMessageLongClick = {}
                        )
                    }
                    Row(
                        modifier = Modifier
                            .align(alignment)
                            .padding(end = 26.dp, start = paddingStart)
                            .clip(RoundedCornerShape(5.dp))
                            .background(MaterialTheme.colorScheme.background)
                            .padding(start = 3.dp, end = 3.dp)
                            .height(65.dp),
                    ) {
                        MessageAction(
                            title = stringResource(id = R.string.chat_action_reply),
                            icon = Icons.Default.Reply
                        ) {
                            onDismissRequest()
                        }
                        MessageAction(
                            title = stringResource(id = R.string.chat_action_forward),
                            icon = ImageVector.vectorResource(id = R.drawable.ic_forward)
                        ) {
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
                            onDismissRequest()
                        }
                    }
                }
            }
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
        Text(title, style = MaterialTheme.typography.labelMedium)
    }
}