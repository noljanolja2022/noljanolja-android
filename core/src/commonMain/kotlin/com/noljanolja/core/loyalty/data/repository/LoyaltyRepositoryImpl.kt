package com.noljanolja.core.loyalty.data.repository

import com.noljanolja.core.loyalty.data.datasource.LoyaltyApi
import com.noljanolja.core.loyalty.domain.model.MemberInfo
import com.noljanolja.core.loyalty.domain.repository.LoyaltyRepository

internal class LoyaltyRepositoryImpl(
    private val loyaltyApi: LoyaltyApi,
) : LoyaltyRepository {
    override suspend fun getMemberInfo(): Result<MemberInfo> {
        return try {
            loyaltyApi.getMemberInfo().data?.let {
                Result.success(it)
            } ?: Result.failure(Throwable("Cannot get member info"))
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }
}