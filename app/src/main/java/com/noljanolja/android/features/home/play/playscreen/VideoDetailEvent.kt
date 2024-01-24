package com.noljanolja.android.features.home.play.playscreen

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer

sealed interface VideoDetailEvent {
    object Back : VideoDetailEvent
    object ToggleFullScreen : VideoDetailEvent
    data class ReadyVideo(val player: YouTubePlayer) : VideoDetailEvent
    data class Comment(val comment: String, val token: String) : VideoDetailEvent
    data class LikeVideo(val isLiked: Boolean, val token: String) : VideoDetailEvent
    data class SendError(val error: Throwable) : VideoDetailEvent
    object TogglePlayPause : VideoDetailEvent
}