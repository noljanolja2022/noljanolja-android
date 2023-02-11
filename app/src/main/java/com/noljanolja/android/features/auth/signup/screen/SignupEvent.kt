package com.noljanolja.android.features.auth.signup.screen


sealed interface SignupEvent {
    data class ChangeEmail(val email: String) : SignupEvent

    data class ChangePassword(val password: String) : SignupEvent

    data class ChangeConfirmPassword(val confirm: String) : SignupEvent

    object Signup : SignupEvent
}