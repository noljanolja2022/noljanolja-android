package com.noljanolja.core.event.data.datasource

import com.noljanolja.core.event.data.model.request.GetBannersRequest
import com.noljanolja.core.event.data.model.response.GetBannersResponse
import com.noljanolja.core.utils.BASE_URL
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class BannerApi(
    private val client: HttpClient,
) {
    suspend fun getBanners(request: GetBannersRequest): GetBannersResponse {
        return client.get("$BASE_URL/api/v1/banners").body()
    }
}