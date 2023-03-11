package com.noljanolja.core.conversation.domain.model

import com.noljanolja.core.user.domain.model.User
import com.noljanolja.core.utils.randomUUID
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val id: Long = 0,
    val localId: String = randomUUID(),
    val sender: User = User(),
    val message: String,
    var stickerUrl: String = "",
    val type: MessageType = MessageType.UNDEFINED,
    val status: MessageStatus = MessageStatus.SENDING,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now(),
)

@Serializable
enum class MessageStatus {
    SENDING,
    SENT,
    VIEWED,
    FAILED,
}

@Serializable
enum class MessageType {
    PLAINTEXT,
    STICKER,
    GIF,
    PHOTO,
    DOCUMENT,
    UNDEFINED,
}
