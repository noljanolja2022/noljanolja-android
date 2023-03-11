package com.noljanolja.core.conversation.data.model.request

import kotlinx.serialization.Serializable

@Serializable
data class GetConversationMessagesRequest(
    val conversationId: Long,
    val messageBefore: Long?,
    val messageAfter: Long?,
)