package com.noljanolja.android.features.chatsettings

sealed interface ChatSettingsEvent {
    object Back : ChatSettingsEvent
}