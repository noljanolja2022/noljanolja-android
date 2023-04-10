package com.noljanolja.core.conversation.data.model.request

@kotlinx.serialization.Serializable
data class AssignAdminRequest(
    val assigneeId: String,
)
