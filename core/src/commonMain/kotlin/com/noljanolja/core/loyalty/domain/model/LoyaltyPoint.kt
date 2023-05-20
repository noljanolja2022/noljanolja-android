package com.noljanolja.core.loyalty.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class LoyaltyPoint(
    val id: String = "",
    val status: LoyaltyStatus = LoyaltyStatus.COMPLETE,
    val amount: Long = 0,
    val reason: String = "",
    val createdAt: Instant = Clock.System.now(),
) {
    val type: LoyaltyType = if (amount >= 0) LoyaltyType.RECEIVE else LoyaltyType.SPENT
}

enum class LoyaltyType {
    RECEIVE,
    SPENT,
}

enum class LoyaltyStatus {
    COMPLETE,
}