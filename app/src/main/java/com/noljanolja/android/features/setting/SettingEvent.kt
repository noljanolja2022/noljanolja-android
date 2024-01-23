package com.noljanolja.android.features.setting

import com.noljanolja.core.file.model.*

sealed interface SettingEvent {
    object Back : SettingEvent
    object ShowLicense : SettingEvent
    object TogglePushNotification : SettingEvent
    object Logout : SettingEvent
    object FAQ : SettingEvent
    object Licence : SettingEvent
    data class ChangeAvatar(val fileInfo: FileInfo) : SettingEvent
}
