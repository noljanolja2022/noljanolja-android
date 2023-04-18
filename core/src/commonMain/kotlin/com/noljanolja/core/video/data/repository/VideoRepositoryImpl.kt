package com.noljanolja.core.video.data.repository

import com.noljanolja.core.video.data.datasource.VideoApi
import com.noljanolja.core.video.data.model.request.GetTrendingVideosRequest
import com.noljanolja.core.video.data.model.request.GetVideoDetailRequest
import com.noljanolja.core.video.data.model.request.GetVideosRequest
import com.noljanolja.core.video.domain.model.TrendingVideoDuration
import com.noljanolja.core.video.domain.model.Video
import com.noljanolja.core.video.domain.repository.VideoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class VideoRepositoryImpl(
    private val videoApi: VideoApi,
) : VideoRepository {
    override fun getTrendingVideo(duration: TrendingVideoDuration): Flow<List<Video>> = flow {
        emit(
            try {
                videoApi.getTrendingVideo(GetTrendingVideosRequest(duration = duration)).data.orEmpty()
            } catch (e: Throwable) {
                emptyList()
            }
        )
    }

    override fun getVideos(isHighlight: Boolean): Flow<List<Video>> = flow {
        emit(
            try {
                videoApi.getVideos(GetVideosRequest(isHighlight = isHighlight)).data.orEmpty()
            } catch (e: Throwable) {
                emptyList()
            }
        )
    }

    override fun getWatchingVideos(): Flow<List<Video>> = flow {
        emit(
            try {
                videoApi.getTrendingVideo(GetTrendingVideosRequest(duration = TrendingVideoDuration.Day)).data.orEmpty()
            } catch (e: Throwable) {
                emptyList()
            }
        )
    }

    override suspend fun getVideoDetail(id: String): Result<Video> {
        return try {
            videoApi.getVideoDetail(GetVideoDetailRequest(videoId = id)).data?.let {
                Result.success(it)
            } ?: throw Throwable("Get video error")
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }
}