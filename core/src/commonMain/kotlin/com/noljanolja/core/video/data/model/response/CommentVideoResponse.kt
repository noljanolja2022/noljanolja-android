package com.noljanolja.core.video.data.model.response

import com.noljanolja.core.base.BaseResponse
import com.noljanolja.core.video.domain.model.Comment
import kotlinx.serialization.Serializable

@Serializable
data class CommentVideoResponse(
    override val code: Int,
    override val message: String,
    override val data: Comment? = null,
) : BaseResponse<Comment>()