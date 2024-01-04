package com.noljanolja.core.shop.data.datasource

import com.noljanolja.core.shop.data.model.request.*
import com.noljanolja.core.shop.data.model.response.*
import com.noljanolja.core.utils.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

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
                    parameters.append("locale", locale)
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
//                    parameters.append("locale", locale)
                }
            }
        }.body()
    }

    suspend fun getGifts(request: GetGiftListRequest): GetGiftsResponse {
        return client.get("$BASE_URL/api/v1/gifts") {
            request.run {
                url {
                    searchText.takeIf { it.isNotBlank() }?.let {
                        parameters.append("query", it)
                    }
                    categoryId.takeIf { it.isNotBlank() }?.let {
                        parameters.append("categoryId", it)
                    }
                    brandId.takeIf { it.isNotBlank() }?.let {
                        parameters.append("brandId", it)
                    }
                    isFeatured?.let {
                        parameters.append("isFeatured", it.toString())
                    }
                    isTodayOffer?.let {
                        parameters.append("isTodayOffer", it.toString())
                    }
                    isRecommended?.let {
                        parameters.append("isRecommended", it.toString())
                    }
                    parameters.append("locale", locale)
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