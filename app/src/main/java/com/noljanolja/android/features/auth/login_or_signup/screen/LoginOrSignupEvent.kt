package com.noljanolja.android.features.auth.login_or_signup.screen

sealed interface LoginOrSignupEvent {
    object SwitchToLogin : LoginOrSignupEvent
    object SwitchSignup : LoginOrSignupEvent
}