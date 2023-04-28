package com.noljanolja.core.video.data.model.request

import kotlinx.serialization.Serializable

@Serializable
data class VideoProgressRequest(
    val videoId: String,
    val event: VideoProgressEvent,
    val durationMs: Long,
)

enum class VideoProgressEvent {
    PLAY, PAUSE, FINISH
}