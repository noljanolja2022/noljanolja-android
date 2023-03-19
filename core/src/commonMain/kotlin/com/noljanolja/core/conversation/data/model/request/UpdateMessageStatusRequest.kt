package com.noljanolja.core.conversation.data.model.request

data class UpdateMessageStatusRequest(
    val conversationId: Long,
    val messageId: Long,
)