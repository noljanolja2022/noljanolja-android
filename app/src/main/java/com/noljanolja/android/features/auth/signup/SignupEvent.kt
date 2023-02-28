package com.noljanolja.android.features.auth.signup

sealed interface SignupEvent {
    data class ChangeEmail(val email: String) : SignupEvent

    data class ChangePassword(val password: String) : SignupEvent

    data class ChangeConfirmPassword(val confirm: String) : SignupEvent

    object Next : SignupEvent

    object Back : SignupEvent

    object Signup : SignupEvent

    object ToggleAllAgreement : SignupEvent

    data class ToggleAgreement(val id: String) : SignupEvent

    data class GoTermsOfService(val id: String) : SignupEvent
}
