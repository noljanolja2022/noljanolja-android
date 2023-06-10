package com.noljanolja.core.shop.data.model.response

import com.noljanolja.core.base.BaseResponse
import com.noljanolja.core.shop.domain.model.Gift
import kotlinx.serialization.Serializable

@Serializable
data class GetGiftResponse(
    override val code: Int,
    override val message: String,
    override val data: Gift? = null,
) : BaseResponse<Gift>()