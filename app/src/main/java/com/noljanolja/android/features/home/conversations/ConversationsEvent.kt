package com.noljanolja.android.features.home.conversations

sealed interface ConversationsEvent {
    data class OpenConversation(
        val conversationId: Long = 0,
    ) : ConversationsEvent

    data class OpenContactPicker(
        val type: String,
    ) : ConversationsEvent

    object ChatSettings : ConversationsEvent
}