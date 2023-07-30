package com.noljanolja.core.conversation.data.model.request

import com.noljanolja.core.conversation.domain.model.Message
import kotlinx.serialization.Serializable

@Serializable
data class SendConversationMessageRequest(
    val conversationId: Long,
    val message: Message,
    val replyToMessageId: Long?,
    val shareMessageId: Long?,
    val shareVideoId: String?,
)

@Serializable
data class SendConversationsMessageRequest(
    val conversationIds: List<Long>,
    val message: Message,
    val replyToMessageId: Long?,
    val shareMessageId: Long?,
    val shareVideoId: String?,
)