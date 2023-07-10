package com.noljanolja.core.conversation.data.model.request

import com.noljanolja.core.conversation.domain.model.Message
import kotlinx.serialization.Serializable

@Serializable
data class SendConversationMessageRequest(
    val conversationId: Long,
    val message: Message,
    val replyToMessageId: Long?,
    val shareMessageId: Long?,
)