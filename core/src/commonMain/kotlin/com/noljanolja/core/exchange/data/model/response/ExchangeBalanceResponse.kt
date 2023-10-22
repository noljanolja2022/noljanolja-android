package com.noljanolja.core.exchange.data.model.response

import com.noljanolja.core.base.BaseResponse
import com.noljanolja.core.exchange.domain.domain.ExchangeBalance
import kotlinx.serialization.Serializable

@Serializable
data class ExchangeBalanceResponse(
    override val code: Int,
    override val message: String,
    override val data: ExchangeBalance = ExchangeBalance(),
) : BaseResponse<ExchangeBalance>()