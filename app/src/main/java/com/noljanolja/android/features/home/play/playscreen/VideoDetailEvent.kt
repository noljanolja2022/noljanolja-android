package com.noljanolja.android.features.home.play.playscreen

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer

sealed interface VideoDetailEvent {
    object Back : VideoDetailEvent
    object ToggleFullScreen : VideoDetailEvent
    data class ReadyVideo(val player: YouTubePlayer) : VideoDetailEvent
}