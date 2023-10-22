package com.noljanolja.core.exchange.data.model.response

import com.noljanolja.core.base.BaseResponse
import com.noljanolja.core.exchange.domain.domain.ExchangeTransaction
import kotlinx.serialization.Serializable

@Serializable
data class ExchangeTransactionResponse(
    override val code: Int,
    override val message: String,
    override val data: ExchangeTransaction = ExchangeTransaction(),
) : BaseResponse<ExchangeTransaction>()