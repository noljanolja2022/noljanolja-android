package com.noljanolja.android.features.setting.more

sealed interface AppInfoEvent {
    object Back : AppInfoEvent
    object AboutUs : AppInfoEvent
}