package com.noljanolja.core.exchange.data.model.response

import com.noljanolja.core.base.BaseResponse
import com.noljanolja.core.exchange.domain.domain.ExchangeRate
import kotlinx.serialization.Serializable

@Serializable
data class ExchangeRateResponse(
    override val code: Int,
    override val message: String,
    override val data: ExchangeRate = ExchangeRate(),
) : BaseResponse<ExchangeRate>()