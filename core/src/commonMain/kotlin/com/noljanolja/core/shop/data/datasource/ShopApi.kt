package com.noljanolja.core.shop.data.datasource

import com.noljanolja.core.base.ResponseWithoutData
import com.noljanolja.core.shop.data.model.request.BuildGiftRequest
import com.noljanolja.core.shop.data.model.request.GetGiftRequest
import com.noljanolja.core.shop.data.model.response.GetGiftsResponse
import com.noljanolja.core.utils.Const
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class ShopApi(private val client: HttpClient) {
    suspend fun getGifts(): GetGiftsResponse {
        return client.get("${Const.BASE_URL}/gifts").body()
    }

    suspend fun getMyGifts(): GetGiftsResponse {
        return client.get("${Const.BASE_URL}/gifts/me").body()
    }

    suspend fun getGiftDetail(request: GetGiftRequest): GetGiftsResponse {
        return client.get("${Const.BASE_URL}/gifts") {
            url {
                parameters.append("giftId", request.id.toString())
            }
        }.body()
    }

    suspend fun buyGift(request: BuildGiftRequest): ResponseWithoutData {
        return client.get("${Const.BASE_URL}/gifts/${request.id}/buy").body()
    }
}