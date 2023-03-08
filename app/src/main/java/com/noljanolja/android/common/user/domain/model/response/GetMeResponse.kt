package com.noljanolja.android.common.user.domain.model.response

import com.noljanolja.core.base.BaseResponse

@kotlinx.serialization.Serializable
data class GetMeResponse(
    override val code: Int,
    override val message: String,
    override val data: UserRemoteModel,
) : BaseResponse<UserRemoteModel>()
