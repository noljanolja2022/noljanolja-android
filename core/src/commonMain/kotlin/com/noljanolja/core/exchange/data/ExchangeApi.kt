package com.noljanolja.core.exchange.data

import com.noljanolja.core.exchange.data.model.request.GetExchangeTransactionsRequest
import com.noljanolja.core.exchange.data.model.response.ExchangeBalanceResponse
import com.noljanolja.core.exchange.data.model.response.ExchangeRateResponse
import com.noljanolja.core.exchange.data.model.response.ExchangeTransactionResponse
import com.noljanolja.core.exchange.data.model.response.ExchangeTransactionsResponse
import com.noljanolja.core.utils.BASE_URL
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post

class ExchangeApi(private val client: HttpClient) {
    suspend fun getRate(): ExchangeRateResponse {
        return client.get("$BASE_URL/api/v1/coin-exchange/rate").body()
    }

    suspend fun getBalance(): ExchangeBalanceResponse {
        return client.get("$BASE_URL/api/v1/coin-exchange/me/balance").body()
    }

    suspend fun getTransactions(request: GetExchangeTransactionsRequest): ExchangeTransactionsResponse {
        return client.get("$BASE_URL/api/v1/coin-exchange/me/balance") {
            request.month?.let {
            }
        }.body()
    }

    suspend fun convert(): ExchangeTransactionResponse {
        return client.post("$BASE_URL/api/v1/coin-exchange/me/convert") {
        }.body()
    }
}