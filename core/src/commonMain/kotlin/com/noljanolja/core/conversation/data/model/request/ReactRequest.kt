package com.noljanolja.core.conversation.data.model.request

import kotlinx.serialization.Serializable

@Serializable
data class ReactRequest(
    val conversationId: Long,
    val messageId: Long,
    val reactId: Long,
)