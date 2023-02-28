package com.noljanolja.android.features.home.info

sealed interface MyInfoEvent {
    object Back : MyInfoEvent
    object Logout : MyInfoEvent
    object GoSetting : MyInfoEvent
}
