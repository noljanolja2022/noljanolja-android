package com.noljanolja.core.loyalty.data.repository

import com.noljanolja.core.loyalty.data.datasource.LoyaltyApi
import com.noljanolja.core.loyalty.domain.model.LoyaltyPoint
import com.noljanolja.core.loyalty.domain.model.MemberInfo
import com.noljanolja.core.loyalty.domain.repository.LoyaltyRepository
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.days

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

    override suspend fun getLoyaltyPoints(): Result<List<LoyaltyPoint>> {
        return try {
            loyaltyApi.getLoyaltyPoints().data.let {
                Result.success(fake())
            }
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }
}

fun fake() = (1..60).map {
    LoyaltyPoint(
        createdAt = Instant.parse("2023-05-28T17:06:03Z").minus((it * 3).days)
    )
}