package com.noljanolja.android.features.home.conversations

sealed interface ConversationsEvent {
    data class OpenConversation(
        val conversationId: Long = 0,
        val userId: String = "",
        val userName: String = "",
    ) : ConversationsEvent

    object OpenContactPicker : ConversationsEvent
}