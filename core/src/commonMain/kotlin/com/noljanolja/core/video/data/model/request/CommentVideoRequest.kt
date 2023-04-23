package com.noljanolja.core.video.data.model.request

import kotlinx.serialization.Serializable

@Serializable
data class CommentVideoRequest(
    val comment: String,
)