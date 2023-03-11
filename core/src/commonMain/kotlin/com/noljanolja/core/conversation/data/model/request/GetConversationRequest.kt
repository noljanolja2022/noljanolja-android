package com.noljanolja.core.conversation.data.model.request

import kotlinx.serialization.Serializable

@Serializable
data class GetConversationRequest(
    val conversationId: Long,
)