package com.noljanolja.core.exchange.data.repository

import com.noljanolja.core.exchange.data.ExchangeApi
import com.noljanolja.core.exchange.data.model.request.ConvertPointRequest
import com.noljanolja.core.exchange.data.model.request.GetExchangeTransactionsRequest
import com.noljanolja.core.exchange.domain.domain.ExchangeBalance
import com.noljanolja.core.exchange.domain.domain.ExchangeRate
import com.noljanolja.core.exchange.domain.domain.ExchangeTransaction
import com.noljanolja.core.exchange.domain.repository.ExchangeRepository

class ExchangeRepositoryImpl(private val exchangeApi: ExchangeApi) : ExchangeRepository {
    override suspend fun convert(): Result<ExchangeTransaction> {
        return try {
            val result = exchangeApi.convert()
            if (result.isSuccessful()) {
                Result.success(result.data)
            } else {
                throw Throwable(result.message)
            }
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    override suspend fun getExchangeTransactions(): Result<List<ExchangeTransaction>> {
        return try {
            val result = exchangeApi.getTransactions(GetExchangeTransactionsRequest())
            if (result.isSuccessful()) {
                Result.success(result.data)
            } else {
                throw Throwable(result.message)
            }
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    override suspend fun getExchangeBalance(): Result<ExchangeBalance> {
        return try {
            val result = exchangeApi.getBalance()
            if (result.isSuccessful()) {
                Result.success(result.data)
            } else {
                throw Throwable(result.message)
            }
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    override suspend fun getRate(): Result<ExchangeRate> {
        return try {
            val result = exchangeApi.getRate()
            if (result.isSuccessful()) {
                Result.success(result.data)
            } else {
                throw Throwable(result.message)
            }
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }
}