package com.noljanolja.core.conversation.data.model.request

import kotlinx.serialization.Serializable

@Serializable
data class UpdateConversationRequest(
    val title: String,
)