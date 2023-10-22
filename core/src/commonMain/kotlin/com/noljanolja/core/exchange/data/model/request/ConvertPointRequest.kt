package com.noljanolja.core.exchange.data.model.request

import kotlinx.serialization.Serializable

@Serializable
data class ConvertPointRequest(
    val point: Long,
)