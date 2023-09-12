package com.noljanolja.android.features.home.play.playlist

sealed interface PlayListEvent {
    object Back : PlayListEvent
    object Refresh : PlayListEvent
    object Search : PlayListEvent
    object Uncompleted : PlayListEvent
}