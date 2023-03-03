package com.noljanolja.android.features.home.chat

import com.noljanolja.android.common.conversation.domain.model.Message
import com.noljanolja.android.common.user.domain.model.User

sealed interface ChatEvent {
    object GoBack : ChatEvent

    data class NavigateToProfile(
        val user: User,
    ) : ChatEvent

    data class SendMessage(
        val message: Message,
    ) : ChatEvent

    data class ClickMessage(
        val message: Message,
    ) : ChatEvent

    object LoadMoreMessages : ChatEvent

    data class ReloadConversation(
        val conversationId: Long,
    ) : ChatEvent
}