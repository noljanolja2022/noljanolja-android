package com.noljanolja.android.features.edit_chat_title

sealed interface EditChatTitleEvent {
    object Back : EditChatTitleEvent
    data class ConfirmEditChatTitle(val name: String) : EditChatTitleEvent
}