package com.noljanolja.android.features.home.chat

import com.noljanolja.core.conversation.domain.model.Message
import com.noljanolja.core.user.domain.model.User

sealed interface ChatEvent {
    object GoBack : ChatEvent

    data class NavigateToProfile(
        val user: User,
    ) : ChatEvent

    data class SendMessage(
        val message: Message,
        val replyToMessageId: Long? = null,
    ) : ChatEvent

    data class ClickMessage(
        val message: Message,
    ) : ChatEvent

    object LoadMoreMessages : ChatEvent

    data class ReloadConversation(
        val conversationId: Long,
    ) : ChatEvent

    object LoadMedia : ChatEvent

    object OpenPhoneSettings : ChatEvent

    object ChatOptions : ChatEvent
    data class React(val messageId: Long, val reactId: Long) : ChatEvent

    data class DeleteMessage(val messageId: Long, val removeForSelfOnly: Boolean) : ChatEvent

    data class ShareMessage(
        val conversationIds: List<Long>? = emptyList(),
        val userIds: List<String>? = emptyList(),
    ) : ChatEvent

    data class SelectShareMessage(val message: Message) : ChatEvent
}