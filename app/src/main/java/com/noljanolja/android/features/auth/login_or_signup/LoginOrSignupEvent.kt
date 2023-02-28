package com.noljanolja.android.features.auth.login_or_signup

sealed interface LoginOrSignupEvent {
    object SwitchToLogin : LoginOrSignupEvent
    object SwitchSignup : LoginOrSignupEvent
    object Back : LoginOrSignupEvent
    object Close : LoginOrSignupEvent
}
