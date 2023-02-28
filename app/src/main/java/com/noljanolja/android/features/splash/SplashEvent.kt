package com.noljanolja.android.features.splash

sealed interface SplashEvent {
    object Continue : SplashEvent
}