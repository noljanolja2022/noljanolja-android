package com.noljanolja.core.video.data.model.response

import com.noljanolja.core.base.BaseResponse
import com.noljanolja.core.video.domain.model.Video
import kotlinx.serialization.Serializable

@Serializable
class GetVideosResponse(
    override val code: Int,
    override val message: String,
    override val data: List<Video>? = null,
) : BaseResponse<List<Video>>()