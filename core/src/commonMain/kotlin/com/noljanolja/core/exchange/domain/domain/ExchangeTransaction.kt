package com.noljanolja.core.exchange.domain.domain

import kotlinx.serialization.Serializable

@Serializable
data class ExchangeTransaction(
    val amount: Double = 0.0,
    val balanceId: Long = 0,
    val createdAt: String = "",
    val id: Long = 0,
    val reason: String = "",
    val status: String = "",
)