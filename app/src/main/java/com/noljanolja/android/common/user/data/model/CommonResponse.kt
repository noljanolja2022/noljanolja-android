package com.noljanolja.android.common.user.data.model

import com.noljanolja.core.base.BaseResponse

data class CommonResponse(
    override val code: Int,
    override val message: String,
) : BaseResponse<Nothing>()