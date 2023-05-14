package com.noljanolja.core.loyalty.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
data class LoyaltyPoint(
    val id: String = "",
    val amount: Int = Random.nextInt(),
    val reason: String = "",
    val createdAt: Instant = Clock.System.now(),
)