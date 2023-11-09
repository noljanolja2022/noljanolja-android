package com.noljanolja.core.shop.data.datasource

import com.noljanolja.core.shop.data.model.request.BuildGiftRequest
import com.noljanolja.core.shop.data.model.request.GetGiftRequest
import com.noljanolja.core.shop.data.model.response.GetGiftResponse
import com.noljanolja.core.shop.data.model.response.GetGiftsResponse
import com.noljanolja.core.utils.BASE_URL
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post

class ShopApi(private val client: HttpClient) {
    suspend fun getGifts(searchText: String): GetGiftsResponse {
        return client.get("$BASE_URL/api/v1/gifts") {
            url {
                searchText.takeIf { it.isNotBlank() }?.let {
                    parameters.append("name", it)
                }
            }
        }.body()
    }

    suspend fun getMyGifts(): GetGiftsResponse {
        return client.get("$BASE_URL/api/v1/gifts/me").body()
    }

    suspend fun getGiftDetail(request: GetGiftRequest): GetGiftResponse {
        return client.get("$BASE_URL/api/v1/gifts/${request.id}").body()
    }

    suspend fun buyGift(request: BuildGiftRequest): GetGiftResponse {
        return client.post("$BASE_URL/api/v1/gifts/${request.id}/buy").body()
    }
}