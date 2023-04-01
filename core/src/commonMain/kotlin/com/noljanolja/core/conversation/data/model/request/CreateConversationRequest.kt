package com.noljanolja.core.conversation.data.model.request

import com.noljanolja.core.conversation.domain.model.ConversationType
import kotlinx.serialization.Serializable

@Serializable
data class CreateConversationRequest(
    val title: String,
    val type: ConversationType? = null,
    val participantIds: List<String> = listOf(),
)