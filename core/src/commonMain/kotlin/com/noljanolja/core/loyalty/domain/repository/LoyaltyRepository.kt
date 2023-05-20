package com.noljanolja.core.loyalty.domain.repository

import com.noljanolja.core.loyalty.domain.model.LoyaltyPoint
import com.noljanolja.core.loyalty.domain.model.LoyaltyType
import com.noljanolja.core.loyalty.domain.model.MemberInfo

interface LoyaltyRepository {
    suspend fun getMemberInfo(): Result<MemberInfo>
    suspend fun getLoyaltyPoints(
        type: LoyaltyType? = null,
        month: Int? = null,
        year: Int? = null,
    ): Result<List<LoyaltyPoint>>
}