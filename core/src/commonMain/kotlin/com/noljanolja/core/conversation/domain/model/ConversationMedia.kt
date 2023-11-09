package com.noljanolja.core.conversation.domain.model

import com.noljanolja.core.utils.BASE_URL
import kotlinx.serialization.Serializable

@Serializable
data class ConversationMedia(
    val attachmentType: String = "",
    val id: Long = 0L,
    val md5: String = "",
    val messageId: Long = 0L,
    val name: String = "",
    val originalName: String = "",
    val previewImage: String = "",
    val size: Long = 0L,
    val type: String = "",
) {

    enum class AttachmentType {
        PHOTO, LINK, FILE
    }

    fun getAttachmentUrl(conversationId: Long) =
        "$BASE_URL/api/v1/conversations/$conversationId/attachments/$id"

    fun getCacheKey() = "attachment$id"
}