package com.noljanolja.core.loyalty.data.repository

import com.noljanolja.core.loyalty.data.datasource.LoyaltyApi
import com.noljanolja.core.loyalty.data.model.request.GetLoyaltyPointsRequest
import com.noljanolja.core.loyalty.domain.model.LoyaltyPoint
import com.noljanolja.core.loyalty.domain.model.LoyaltyType
import com.noljanolja.core.loyalty.domain.model.MemberInfo
import com.noljanolja.core.loyalty.domain.repository.LoyaltyRepository
import com.noljanolja.core.user.data.datasource.LocalUserDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.days

internal class LoyaltyRepositoryImpl(
    private val loyaltyApi: LoyaltyApi,
    private val userDataSource: LocalUserDataSource,
) : LoyaltyRepository {
    override fun getMemberInfo(): Flow<MemberInfo> {
        return userDataSource.getMemberInfo().onStart {
            refreshMemberInfo()
        }
    }

    override suspend fun refreshMemberInfo() {
        try {
            loyaltyApi.getMemberInfo().data.let {
                userDataSource.upsertMemberInfo(it!!)
            }
        } catch (e: Throwable) {
            userDataSource.upsertMemberInfo(MemberInfo())
        }
    }

    override suspend fun getLoyaltyPoints(
        type: LoyaltyType?,
        month: Int?,
        year: Int?,
    ): Result<List<LoyaltyPoint>> {
        return try {
            loyaltyApi.getLoyaltyPoints(
                GetLoyaltyPointsRequest(
                    type = when (type) {
                        null -> GetLoyaltyPointsRequest.FilterType.ALL
                        LoyaltyType.RECEIVE -> GetLoyaltyPointsRequest.FilterType.RECEIVE
                        LoyaltyType.SPENT -> GetLoyaltyPointsRequest.FilterType.SPENT
                    },
                    month = month,
                    year = year
                )
            ).data.let {
                Result.success(it)
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