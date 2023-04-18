package com.noljanolja.core.video.data.datasource

import com.noljanolja.core.utils.Const
import com.noljanolja.core.video.data.model.request.GetTrendingVideosRequest
import com.noljanolja.core.video.data.model.request.GetVideoDetailRequest
import com.noljanolja.core.video.data.model.request.GetVideosRequest
import com.noljanolja.core.video.data.model.response.GetVideoResponse
import com.noljanolja.core.video.data.model.response.GetVideosResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

internal class VideoApi(private val client: HttpClient) {
    suspend fun getTrendingVideo(request: GetTrendingVideosRequest): GetVideosResponse {
        return client.get("${Const.BASE_URL}/media/videos/trending?duration=${request.duration.name.lowercase()}")
            .body()
    }

    suspend fun getVideos(request: GetVideosRequest): GetVideosResponse {
        return client.get("${Const.BASE_URL}/media/videos?isHighlighted=${request.isHighlight}")
            .body()
    }

    suspend fun getVideoDetail(request: GetVideoDetailRequest): GetVideoResponse {
        return client.get("${Const.BASE_URL}/media/videos/${request.videoId}").body()
    }
}