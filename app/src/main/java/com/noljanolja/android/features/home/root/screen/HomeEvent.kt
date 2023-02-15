package com.noljanolja.android.features.home.root.screen

sealed interface HomeEvent {
    data class ChangeNavigationItem(
        val item: HomeNavigationItem,
        val onChange: () -> Unit
    ) : HomeEvent

    object LoginOrVerifyEmail : HomeEvent
}
