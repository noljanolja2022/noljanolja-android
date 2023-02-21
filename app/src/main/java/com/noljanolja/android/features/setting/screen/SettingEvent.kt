package com.noljanolja.android.features.setting.screen

sealed interface SettingEvent {
    object Back : SettingEvent
    object ClearCacheData : SettingEvent
    object ShowLicense : SettingEvent
    object TogglePushNotification : SettingEvent
}
