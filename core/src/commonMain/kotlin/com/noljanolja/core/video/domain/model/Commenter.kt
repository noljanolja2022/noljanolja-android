package com.noljanolja.core.video.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Commenter(
    val avatar: String = "",
    val name: String = "",
)