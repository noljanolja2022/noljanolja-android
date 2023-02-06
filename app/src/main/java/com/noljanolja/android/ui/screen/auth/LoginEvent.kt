package com.noljanolja.android.ui.screen.auth

sealed interface LoginEvent {
    object GoToMain : LoginEvent
    data class ShowError(val error: Throwable?) : LoginEvent
}