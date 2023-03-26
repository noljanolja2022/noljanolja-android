package com.noljanolja.core.conversation.domain.model

import com.noljanolja.core.user.domain.model.User
import com.noljanolja.core.utils.Const.BASE_URL
import com.noljanolja.core.utils.randomUUID
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Message(
    val id: Long = 0,
    @SerialName("localId")
    private val _localId: String = randomUUID(),
    val sender: User = User(),
    val message: String,
    var stickerUrl: String = "",
    val attachments: List<MessageAttachment> = listOf(),
    val type: MessageType = MessageType.UNDEFINED,
    val status: MessageStatus = MessageStatus.SENDING,
    val seenBy: List<String> = emptyList(),
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now(),
) {
    val localId: String get() = _localId.takeIf { it.isNotBlank() } ?: randomUUID()
    var seenUsers: List<User> = emptyList()
    var isSeenByMe: Boolean = false
}

@Serializable
data class MessageAttachment(
    val id: Long = 0,
    val name: String,
    val originalName: String,
    val type: String,
    val size: Long = 0,
    var url: String = "",
    var localPath: String = "",
    @Transient
    val contents: ByteArray = ByteArray(0),
) {
    fun getAttachmentUrl(conversationId: Long) =
        "$BASE_URL/conversations/$conversationId/attachments/$id"

    fun getPhotoUri(conversationId: Long) =
        localPath.takeIf { it.isNotBlank() } ?: getAttachmentUrl(
            conversationId
        )
}

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
