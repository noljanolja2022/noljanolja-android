package com.noljanolja.android.features.home.play.playlist

sealed interface PlayListEvent {
    object Back : PlayListEvent

    data class PlayVideo(val id: String) : PlayListEvent

    object Refresh : PlayListEvent

    object Search : PlayListEvent

    object Uncompleted : PlayListEvent

    object Setting : PlayListEvent

    object OpenNotificationScreen : PlayListEvent
}