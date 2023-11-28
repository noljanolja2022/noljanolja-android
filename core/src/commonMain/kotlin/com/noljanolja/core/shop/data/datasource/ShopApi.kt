package com.noljanolja.core.shop.data.datasource

import com.noljanolja.core.shop.data.model.request.*
import com.noljanolja.core.shop.data.model.response.*
import com.noljanolja.core.utils.BASE_URL
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post

class ShopApi(private val client: HttpClient) {
    suspend fun getBrands(request: GetItemChooseRequest): GetItemChooseResponse {
        return client.get("$BASE_URL/api/v1/gifts/brands") {
            url {
                request.run {
                    parameters.append("page", page.toString())
                    parameters.append("pageSize", pageSize.toString())
                    query?.let {
                        parameters.append("query", it)
                    }
                }
            }
        }.body()
    }

    suspend fun getCategories(request: GetItemChooseRequest): GetItemChooseResponse {
        return client.get("$BASE_URL/api/v1/gifts/categories") {
            url {
                request.run {
                    parameters.append("page", page.toString())
                    parameters.append("pageSize", pageSize.toString())
                    query?.let {
                        parameters.append("query", it)
                    }
                }
            }
        }.body()
    }

    suspend fun getGifts(
        searchText: String,
        categoryId: String,
        isFeatured: Boolean?,
        isTodayOffer: Boolean?
    ): GetGiftsResponse {
        return client.get("$BASE_URL/api/v1/gifts") {
            url {
                searchText.takeIf { it.isNotBlank() }?.let {
                    parameters.append("query", it)
                }
                categoryId.takeIf { it.isNotBlank() }?.let {
                    parameters.append("categoryId", it)
                }
                isFeatured?.let {
                    parameters.append("isFeatured", it.toString())
                }
                isTodayOffer?.let {
                    parameters.append("isTodayOffer", it.toString())
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