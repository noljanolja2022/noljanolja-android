package com.noljanolja.android.features.home.root

sealed interface HomeEvent {
    object LoginOrVerifyEmail : HomeEvent
    object CancelBanner : HomeEvent
    data class CloseBanner(val id: Long) : HomeEvent
    object Back : HomeEvent
    data class AutoAction(val token: String) : HomeEvent
}
