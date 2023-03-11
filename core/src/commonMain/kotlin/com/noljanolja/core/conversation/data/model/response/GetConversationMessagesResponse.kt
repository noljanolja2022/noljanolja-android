package com.noljanolja.core.conversation.data.model.response

import com.noljanolja.core.base.BaseResponse
import com.noljanolja.core.conversation.domain.model.Message
import kotlinx.serialization.Serializable

@Serializable
data class GetConversationMessagesResponse(
    override val code: Int,
    override val message: String,
    override val data: List<Message>? = null,
) : BaseResponse<List<Message>>()