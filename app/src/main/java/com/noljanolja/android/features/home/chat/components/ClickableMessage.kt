package com.noljanolja.android.features.home.chat.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.noljanolja.android.common.conversation.domain.model.Message
import com.noljanolja.android.common.conversation.domain.model.MessageType

@Composable
fun ClickableMessage(
    conversationId: Long,
    message: Message,
    onMessageClick: (Message) -> Unit,
) {
    when (message.type) {
        MessageType.PlainText -> {
            ClickableTextMessage(
                message = message,
                modifier = Modifier.clickable { onMessageClick(message) }
                    .padding(vertical = 16.dp, horizontal = 12.dp),
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
            color = if (message.sender.isMe) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
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