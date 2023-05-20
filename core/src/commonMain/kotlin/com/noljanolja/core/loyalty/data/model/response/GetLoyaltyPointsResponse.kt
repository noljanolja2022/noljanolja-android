package com.noljanolja.core.loyalty.data.model.response

import com.noljanolja.core.base.BaseResponse
import com.noljanolja.core.loyalty.domain.model.LoyaltyPoint

@kotlinx.serialization.Serializable
internal data class GetLoyaltyPointsResponse(
    override val code: Int,
    override val message: String,
    override val data: List<LoyaltyPoint> = emptyList(),
) : BaseResponse<List<LoyaltyPoint>>()