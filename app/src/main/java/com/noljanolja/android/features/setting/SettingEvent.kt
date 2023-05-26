package com.noljanolja.android.features.setting

sealed interface SettingEvent {
    object Back : SettingEvent
    object ClearCacheData : SettingEvent
    object ShowLicense : SettingEvent
    object TogglePushNotification : SettingEvent
    object Logout : SettingEvent
    object FAQ : SettingEvent
    object Licence : SettingEvent
}
