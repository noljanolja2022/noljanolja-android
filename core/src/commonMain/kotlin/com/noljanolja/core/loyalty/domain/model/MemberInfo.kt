package com.noljanolja.core.loyalty.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class MemberInfo(
    val currentTier: String,
    val currentTierMinPoint: Int,
    val memberId: String,
    val nextTier: String,
    val nextTierMinPoint: Int,
    val point: Int,
)