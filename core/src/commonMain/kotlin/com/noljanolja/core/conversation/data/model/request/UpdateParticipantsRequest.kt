package com.noljanolja.core.conversation.data.model.request

@kotlinx.serialization.Serializable
data class UpdateParticipantsRequest(
    val participantIds: List<String>,
)
