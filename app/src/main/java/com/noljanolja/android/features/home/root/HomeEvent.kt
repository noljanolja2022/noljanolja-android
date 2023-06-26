package com.noljanolja.android.features.home.root

sealed interface HomeEvent {
    object LoginOrVerifyEmail : HomeEvent
    object CancelBanner : HomeEvent
}
