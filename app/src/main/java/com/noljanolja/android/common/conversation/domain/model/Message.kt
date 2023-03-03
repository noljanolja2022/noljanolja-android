package com.noljanolja.android.common.conversation.domain.model

import com.noljanolja.android.common.user.domain.model.User
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import java.util.UUID.randomUUID

@Serializable
data class Message(
    val id: Long = 0,
    val localId: String = randomUUID().toString(),
    val sender: User = User(),
    val message: String,
    var stickerUrl: String = "",
    val type: MessageType = MessageType.Undefined,
    val status: MessageStatus = MessageStatus.Sending,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now(),
)

@Serializable
enum class MessageStatus {
    Sending,
    Sent,
    Viewed,
    Failed,
}

@Serializable
enum class MessageType {
    PlainText,
    Sticker,
    Gif,
    Photo,
    Document,
    Undefined,
}
