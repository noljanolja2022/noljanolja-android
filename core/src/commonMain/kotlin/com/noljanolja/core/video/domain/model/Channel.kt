package com.noljanolja.core.video.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Channel(
    val id: String = "",
    val thumbnail: String = "",
    val title: String = "",
)