package com.noljanolja.android.features.auth.login

sealed interface LoginEvent {
    object GoJoinMember : LoginEvent

    data class ChangeEmail(val email: String) : LoginEvent

    data class ChangePassword(val password: String) : LoginEvent

    object GoForgotEmailAndPassword : LoginEvent

    object LoginEmail : LoginEvent

    object LoginKakao : LoginEvent
    data class ShowError(val error: Throwable?) : LoginEvent

    object Back : LoginEvent

    object VerifyEmail : LoginEvent

    object OpenCountryList : LoginEvent

    data class SendOTP(val phone: String) : LoginEvent

    data class HandleLoginResult(val token: String) : LoginEvent
}
