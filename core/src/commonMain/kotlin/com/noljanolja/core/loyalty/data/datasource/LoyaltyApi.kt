package com.noljanolja.core.loyalty.data.datasource

import com.noljanolja.core.loyalty.data.model.response.GetMemberInfoResponse
import com.noljanolja.core.utils.Const
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

internal class LoyaltyApi(
    private val client: HttpClient,
) {
    suspend fun getMemberInfo(): GetMemberInfoResponse {
        return client.get("${Const.BASE_URL}/loyalty/me").body()
    }
}