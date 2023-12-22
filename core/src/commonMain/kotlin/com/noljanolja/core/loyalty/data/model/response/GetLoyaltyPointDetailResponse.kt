package com.noljanolja.core.loyalty.data.model.response

import com.noljanolja.core.base.*
import com.noljanolja.core.loyalty.domain.model.*

/**
 * Created by tuyen.dang on 12/22/2023.
 */

@kotlinx.serialization.Serializable
data class GetLoyaltyPointDetailResponse(
    override val code: Int,
    override val message: String,
    override val data: LoyaltyPoint = LoyaltyPoint(),
) : BaseResponse<LoyaltyPoint>()
