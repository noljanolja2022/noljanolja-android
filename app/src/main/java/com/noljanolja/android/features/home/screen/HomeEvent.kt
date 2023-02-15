package com.noljanolja.android.features.home.screen

sealed interface HomeEvent {
    data class ChangeNavigationItem(
        val item: HomeNavigationItem,
        val onChange: () -> Unit
    ) : HomeEvent

    object GoToLogin : HomeEvent
}