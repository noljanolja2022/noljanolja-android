package com.noljanolja.android.features.auth.forget.screen

sealed interface ForgotEvent {
    object Back : ForgotEvent
    object VerifyEmail : ForgotEvent
    data class ChangeEmail(val email: String) : ForgotEvent
    object ResendPassword : ForgotEvent
    object Close : ForgotEvent
}
