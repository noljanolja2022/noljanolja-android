package com.noljanolja.core.conversation.domain.model

import com.noljanolja.core.utils.Const
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
        "${Const.BASE_URL}/conversations/$conversationId/attachments/$id"

    fun getCacheKey() = "attachment$id"
}