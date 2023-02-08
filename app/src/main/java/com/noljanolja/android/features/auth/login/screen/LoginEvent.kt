package com.noljanolja.android.features.auth.login.screen

sealed interface LoginEvent {
    object GoToMain : LoginEvent
    object GoToSignup : LoginEvent
    data class ShowError(val error: Throwable?) : LoginEvent
}
