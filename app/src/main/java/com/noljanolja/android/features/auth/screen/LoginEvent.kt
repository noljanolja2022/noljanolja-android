package com.noljanolja.android.features.auth.screen

sealed interface LoginEvent {
    object GoToMain : LoginEvent
    data class ShowError(val error: Throwable?) : LoginEvent
}