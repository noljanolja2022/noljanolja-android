package com.noljanolja.core.video.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PromotedVideo(
    val autoComment: Boolean = false,
    val autoLike: Boolean = false,
    val autoPlay: Boolean = false,
    val autoSubscribe: Boolean = false,
    val createdAt: String = "",
    val endDate: String = "",
    val id: String = "",
    val startDate: String = "",
    val updatedAt: String = "",
    val video: Video = Video(),
)