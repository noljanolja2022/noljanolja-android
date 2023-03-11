package com.noljanolja.core.conversation.data.model.response

import com.noljanolja.core.base.BaseResponse
import com.noljanolja.core.conversation.domain.model.Conversation
import kotlinx.serialization.Serializable

@Serializable
data class CreateConversationResponse(
    override val code: Int,
    override val message: String,
    override val data: Conversation? = null,
) : BaseResponse<Conversation>()