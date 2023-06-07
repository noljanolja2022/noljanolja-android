package com.noljanolja.android.features.shop.search

sealed interface SearchProductEvent {
    object Back : SearchProductEvent
    data class Search(val text: String) : SearchProductEvent
    object ClearAll : SearchProductEvent
    data class Clear(val text: String) : SearchProductEvent
}