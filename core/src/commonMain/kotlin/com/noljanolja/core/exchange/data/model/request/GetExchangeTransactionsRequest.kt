package com.noljanolja.core.exchange.data.model.request

data class GetExchangeTransactionsRequest(
    val type: String = "ALL",
    val month: Int? = null,
    val year: Int? = null,
)