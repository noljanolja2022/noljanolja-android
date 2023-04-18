package com.noljanolja.core.video.data.model.request

import com.noljanolja.core.video.domain.model.TrendingVideoDuration

data class GetTrendingVideosRequest(
    val duration: TrendingVideoDuration = TrendingVideoDuration.Day,
    val limit: Int = 100,
)

