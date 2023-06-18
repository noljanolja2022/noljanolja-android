package com.noljanolja.core.conversation.data.model.response

import com.noljanolja.core.base.BaseResponse
import com.noljanolja.core.conversation.domain.model.ReactIcon
import kotlinx.serialization.Serializable

@Serializable
data class GetReactIconsResponse(
    override val code: Int,
    override val message: String,
    override val data: List<ReactIcon> = emptyList(),
) : BaseResponse<List<ReactIcon>>()