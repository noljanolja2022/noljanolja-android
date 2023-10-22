package com.noljanolja.core.exchange.data.model.response

import com.noljanolja.core.base.BaseResponse
import com.noljanolja.core.exchange.domain.domain.ExchangeTransaction
import kotlinx.serialization.Serializable

@Serializable
data class ExchangeTransactionsResponse(
    override val code: Int,
    override val message: String,
    override val data: List<ExchangeTransaction> = emptyList(),
) : BaseResponse<List<ExchangeTransaction>>()