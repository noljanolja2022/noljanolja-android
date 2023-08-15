package com.noljanolja.android.features.home.play.uncompleted

sealed interface UncompletedEvent {
    object Back : UncompletedEvent

    data class PlayVideo(val id: String) : UncompletedEvent
}