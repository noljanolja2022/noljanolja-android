package com.noljanolja.core.user.data.model.response

import com.noljanolja.core.base.BaseResponse

@kotlinx.serialization.Serializable
data class GetMeResponse(
    override val code: Int,
    override val message: String,
    override val data: UserRemoteModel,
) : BaseResponse<UserRemoteModel>()
