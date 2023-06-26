package com.noljanolja.core.event.domain.repository

import com.noljanolja.core.event.domain.model.EventBanner

interface EventBannerRepository {
    suspend fun getEventBanners(): Result<List<EventBanner>>
}