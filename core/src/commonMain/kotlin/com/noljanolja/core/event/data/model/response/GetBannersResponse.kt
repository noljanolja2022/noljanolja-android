package com.noljanolja.core.event.data.model.response

import com.noljanolja.core.base.BaseResponse
import com.noljanolja.core.event.domain.model.EventBanner
import kotlinx.serialization.Serializable

@Serializable
data class GetBannersResponse(
    override val code: Int,
    override val message: String,
    override val data: List<EventBanner> = emptyList(),
) : BaseResponse<List<EventBanner>>()