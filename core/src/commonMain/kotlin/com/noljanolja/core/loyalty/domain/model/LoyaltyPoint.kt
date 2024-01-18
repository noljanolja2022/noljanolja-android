package com.noljanolja.core.loyalty.domain.model

import kotlinx.datetime.*
import kotlinx.serialization.*

@Serializable
data class LoyaltyPoint(
    val id: String = "",
    val status: LoyaltyStatus = LoyaltyStatus.COMPLETED,
    val amount: Long = 0,
    val reasonLocale: String = "",
    val reason: String = "",
    val unit: String = "",
    val createdAt: Instant = Clock.System.now(),
    val log: String = ""
) {
    val type: LoyaltyType = if (amount >= 0) LoyaltyType.RECEIVE else LoyaltyType.SPENT
}

enum class LoyaltyType {
    RECEIVE,
    SPENT,
}

enum class LoyaltyStatus {
    COMPLETED,
    FAILLED
}