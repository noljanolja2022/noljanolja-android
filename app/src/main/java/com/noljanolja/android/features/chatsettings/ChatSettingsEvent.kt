package com.noljanolja.android.features.chatsettings

import com.noljanolja.core.file.model.FileInfo

sealed interface ChatSettingsEvent {
    object Back : ChatSettingsEvent
    data class ChangeAvatar(val fileInfo: FileInfo) : ChatSettingsEvent
}