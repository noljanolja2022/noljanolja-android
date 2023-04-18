package com.noljanolja.core.video.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: String = "",
    val title: String = "",
)