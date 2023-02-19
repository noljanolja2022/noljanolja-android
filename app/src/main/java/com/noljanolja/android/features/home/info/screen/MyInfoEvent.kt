package com.noljanolja.android.features.home.info.screen

sealed interface MyInfoEvent {
    object Back : MyInfoEvent
    object Logout : MyInfoEvent
}
