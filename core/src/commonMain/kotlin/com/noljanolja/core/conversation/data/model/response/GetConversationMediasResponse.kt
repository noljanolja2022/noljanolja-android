package com.noljanolja.core.conversation.data.model.response

import com.noljanolja.core.base.BaseResponse
import com.noljanolja.core.conversation.domain.model.ConversationMedia
import kotlinx.serialization.Serializable

@Serializable
data class GetConversationMediasResponse(
    override val code: Int,
    override val message: String,
    override val data: List<ConversationMedia>? = null,
) : BaseResponse<List<ConversationMedia>>()