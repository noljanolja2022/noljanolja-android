package com.noljanolja.core.video.data.model.response

import com.noljanolja.core.base.BaseResponse
import com.noljanolja.core.video.domain.model.PromotedVideo
import kotlinx.serialization.Serializable

@Serializable
class GetPromotedVideosResponse(
    override val code: Int,
    override val message: String,
    override val data: List<PromotedVideo>? = null,
) : BaseResponse<List<PromotedVideo>>()