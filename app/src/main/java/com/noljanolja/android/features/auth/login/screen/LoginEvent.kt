package com.noljanolja.android.features.auth.login.screen

sealed interface LoginEvent {
    object GoToMain : LoginEvent
    object GoJoinMember : LoginEvent

    data class ChangeEmail(val email: String) : LoginEvent

    data class ChangePassword(val password: String) : LoginEvent

    object GoForgotEmailAndPassword : LoginEvent

    object LoginEmail : LoginEvent

    object LoginKakao : LoginEvent

    object LoginNaver : LoginEvent
    data class ShowError(val error: Throwable?) : LoginEvent
}
