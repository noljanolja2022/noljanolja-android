package com.noljanolja.core.exchange.domain.domain

import kotlinx.serialization.Serializable

@Serializable
data class ExchangeBalance(
    val balance: Double = 0.0,
)