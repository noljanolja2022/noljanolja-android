package com.noljanolja.core.user.data.model.response

import com.noljanolja.core.base.BaseResponse

data class ResponseWithoutData(
    override val code: Int,
    override val message: String,
) : BaseResponse<Nothing>()