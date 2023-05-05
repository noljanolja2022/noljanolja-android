package com.noljanolja.core.conversation.domain.model

import com.noljanolja.core.user.domain.model.User
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Conversation(
    val id: Long = 0L,
    val title: String = "",
    val type: ConversationType = ConversationType.SINGLE,
    val creator: User = User(),
    val admin: User = User(),
    val participants: List<User> = listOf(),
    val messages: List<Message> = listOf(),
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now(),
) {
    fun getDisplayTitle(): String {
        return if (type == ConversationType.SINGLE) {
            (participants.find { !it.isMe } ?: participants.firstOrNull())?.name.orEmpty()
        } else {
            title.takeIf { it.isNotBlank() } ?: participants.joinToString(", ") { it.name }
        }
    }

    fun getDisplayAvatarUrl(): String? {
        return if (type == ConversationType.SINGLE) {
            (participants.find { !it.isMe } ?: participants.firstOrNull())?.getAvatarUrl()
                .orEmpty()
        } else {
            null
        }
    }

    fun getSingleReceiver(): User? = participants.find { !it.isMe }.takeIf { type == ConversationType.SINGLE }
}

@Serializable
enum class ConversationType {
    SINGLE, GROUP
}