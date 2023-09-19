package com.noljanolja.core.video.data.model.request

import kotlinx.serialization.Serializable

@Serializable
data class LikeVideoRequest(
    val action: String = "like",
    val youtubeToken: String,
)