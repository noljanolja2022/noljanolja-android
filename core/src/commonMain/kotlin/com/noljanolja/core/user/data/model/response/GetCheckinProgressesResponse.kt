package com.noljanolja.core.user.data.model.response

import com.noljanolja.core.base.BaseResponse
import com.noljanolja.core.user.domain.model.CheckinProgress

@kotlinx.serialization.Serializable
data class GetCheckinProgressesResponse(
    override val code: Int,
    override val message: String,
    override val data: List<CheckinProgress> = emptyList(),
) : BaseResponse<List<CheckinProgress>>()