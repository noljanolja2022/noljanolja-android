package com.noljanolja.core.conversation.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ReactIcon(
    val id: Long = 0L,
    val code: String = "",
    val description: String = "",
)