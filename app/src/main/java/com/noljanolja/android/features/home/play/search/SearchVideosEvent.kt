package com.noljanolja.android.features.home.play.search

sealed interface SearchVideosEvent {
    data class Search(val text: String) : SearchVideosEvent
    object ClearAll : SearchVideosEvent
    data class Clear(val text: String) : SearchVideosEvent
}