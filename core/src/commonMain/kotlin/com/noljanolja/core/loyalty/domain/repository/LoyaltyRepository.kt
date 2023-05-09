package com.noljanolja.core.loyalty.domain.repository

import com.noljanolja.core.loyalty.domain.model.MemberInfo

interface LoyaltyRepository {
    suspend fun getMemberInfo(): Result<MemberInfo>
}