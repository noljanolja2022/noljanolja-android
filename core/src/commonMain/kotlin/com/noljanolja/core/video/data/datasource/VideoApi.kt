package com.noljanolja.core.video.data.datasource

import com.noljanolja.core.utils.Const
import com.noljanolja.core.video.data.model.request.CommentVideoRequest
import com.noljanolja.core.video.data.model.request.GetTrendingVideosRequest
import com.noljanolja.core.video.data.model.request.GetVideoDetailRequest
import com.noljanolja.core.video.data.model.request.GetVideosRequest
import com.noljanolja.core.video.data.model.response.CommentVideoResponse
import com.noljanolja.core.video.data.model.response.GetVideoResponse
import com.noljanolja.core.video.data.model.response.GetVideosResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody

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

    suspend fun commentVideo(videoId: String, request: CommentVideoRequest): CommentVideoResponse {
        return client.post("${Const.BASE_URL}/media/videos/$videoId/comments") {
            setBody(request)
        }.body()
    }
}