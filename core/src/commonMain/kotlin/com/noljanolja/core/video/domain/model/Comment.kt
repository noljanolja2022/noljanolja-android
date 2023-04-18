package com.noljanolja.core.video.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Comment(
    val comment: String = "",
    val commenter: Commenter = Commenter(),
    val id: Int = 0,
)