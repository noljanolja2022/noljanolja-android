package com.noljanolja.android.ui.screen.auth

sealed interface LoginEvent {
    object GoToMain : LoginEvent
}