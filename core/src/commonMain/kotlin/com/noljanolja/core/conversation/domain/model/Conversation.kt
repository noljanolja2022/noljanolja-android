package com.noljanolja.core.conversation.domain.model

import com.noljanolja.core.user.domain.model.User
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
data class Conversation(
    val id: Long = 0L,
    val title: String = "",
    val type: ConversationType = ConversationType.SINGLE,
    val creator: User = User(),
    val participants: List<User> = listOf(),
    val messages: List<Message> = listOf(),
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now(),
) {
    fun getDisplayTitle(): String {
        return if (type == ConversationType.SINGLE) {
            (participants.find { !it.isMe } ?: participants.firstOrNull())?.name.orEmpty()
        } else {
            title
        }
    }

    fun getDisplayAvatarUrl(): String {
        return if (type == ConversationType.SINGLE) {
            (participants.find { !it.isMe } ?: participants.firstOrNull())?.getAvatarUrl()
                .orEmpty()
        } else {
            ""
        }
    }

    companion object {
        fun mock(): Conversation {
            val random = Random.nextInt(100_000_000)
            return Conversation(
                id = 0,
                title = random.toString(),
                type = ConversationType.SINGLE,
                creator = User(
                    name = random.toString()
                ),
                messages = listOf(
                    Message(
                        message = "Hello, I'm $random",
                        type = MessageType.PLAINTEXT
                    )
                ),
                participants = listOf(
                    User(
                        name = random.toString()
                    )
                )
            )
        }
    }
}

@Serializable
enum class ConversationType {
    SINGLE, GROUP
}