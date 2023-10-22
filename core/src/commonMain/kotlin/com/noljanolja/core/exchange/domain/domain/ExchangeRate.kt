package com.noljanolja.core.exchange.domain.domain

import kotlinx.serialization.Serializable

@Serializable
data class ExchangeRate(
    val coinToPointRate: Long = 0L,
    val rewardRecurringAmount: Long = 0L,
)