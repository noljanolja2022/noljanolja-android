package com.noljanolja.core.exchange.domain.repository

import com.noljanolja.core.exchange.domain.domain.ExchangeBalance
import com.noljanolja.core.exchange.domain.domain.ExchangeRate
import com.noljanolja.core.exchange.domain.domain.ExchangeTransaction

interface ExchangeRepository {
    suspend fun convert(): Result<ExchangeTransaction>
    suspend fun getExchangeTransactions(): Result<List<ExchangeTransaction>>

    suspend fun getExchangeBalance(): Result<ExchangeBalance>

    suspend fun getRate(): Result<ExchangeRate>
}