package com.noljanolja.core.video.data.model.request

import kotlinx.serialization.Serializable

@Serializable
data class GetVideosRequest(
    val query: String? = null,
    val isHighlight: Boolean? = null,
)