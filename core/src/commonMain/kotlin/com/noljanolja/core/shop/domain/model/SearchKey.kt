package com.noljanolja.core.shop.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class SearchKey(
    val text: String = "",
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now(),
)