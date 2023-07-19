package com.noljanolja.android.features.home.chat_options

sealed interface ChatOptionsEvent {
    object Back : ChatOptionsEvent

    object AddContact : ChatOptionsEvent

    object EditTitle : ChatOptionsEvent

    object LeaveConversation : ChatOptionsEvent

    data class RemoveParticipant(val id: String) : ChatOptionsEvent

    data class MakeAdminConversation(val id: String) : ChatOptionsEvent

    data class BlockUser(val id: String) : ChatOptionsEvent
}