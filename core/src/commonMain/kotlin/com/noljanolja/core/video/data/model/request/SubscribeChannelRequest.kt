package com.noljanolja.core.video.data.model.request

import kotlinx.serialization.Serializable

@Serializable
data class SubscribeChannelRequest(
    val isSubscribing: Boolean,
    val youtubeToken: String,
)