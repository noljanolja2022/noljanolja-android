package com.noljanolja.core.loyalty.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class MemberInfo(
    val currentTier: MemberTier = MemberTier.BRONZE,
    val currentTierMinPoint: Long = 0,
    val memberId: String = "",
    val nextTier: MemberTier? = null,
    val nextTierMinPoint: Long = 0,
    val point: Long = 0,
)

enum class MemberTier {
    BRONZE, SILVER, GOLD, PREMIUM
}