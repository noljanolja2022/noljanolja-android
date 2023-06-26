package com.noljanolja.core.event.data.repository

import com.noljanolja.core.event.data.datasource.BannerApi
import com.noljanolja.core.event.data.model.request.GetBannersRequest
import com.noljanolja.core.event.domain.model.EventBanner
import com.noljanolja.core.event.domain.repository.EventBannerRepository

class EventBannerRepositoryImpl(private val bannerApi: BannerApi) : EventBannerRepository {
    override suspend fun getEventBanners(): Result<List<EventBanner>> {
        val response = bannerApi.getBanners(GetBannersRequest())
        return try {
            if (response.isSuccessful()) {
                Result.success(response.data)
            } else {
                throw Throwable(response.message)
            }
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }
}