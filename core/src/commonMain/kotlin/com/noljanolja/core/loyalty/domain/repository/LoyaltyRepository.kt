package com.noljanolja.core.loyalty.domain.repository

import com.noljanolja.core.loyalty.domain.model.LoyaltyPoint
import com.noljanolja.core.loyalty.domain.model.LoyaltyType
import com.noljanolja.core.loyalty.domain.model.MemberInfo
import kotlinx.coroutines.flow.Flow

interface LoyaltyRepository {
    fun getMemberInfo(): Flow<MemberInfo>

    suspend fun refreshMemberInfo()
    suspend fun getLoyaltyPoints(
        type: LoyaltyType? = null,
        month: Int? = null,
        year: Int? = null,
    ): Result<List<LoyaltyPoint>>
}