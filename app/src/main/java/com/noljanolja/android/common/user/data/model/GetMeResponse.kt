package com.noljanolja.android.common.user.data.model

import com.noljanolja.android.common.base.BaseResponse

@kotlinx.serialization.Serializable
data class GetMeResponse(
    override val code: Int,
    override val message: String,
    override val data: UserRemoteModel,
) : BaseResponse<UserRemoteModel>()
