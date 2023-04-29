package com.noljanolja.core.video.data.repository

import com.noljanolja.core.video.data.datasource.LocalVideoDataSource
import com.noljanolja.core.video.data.datasource.VideoApi
import com.noljanolja.core.video.data.model.request.CommentVideoRequest
import com.noljanolja.core.video.data.model.request.GetTrendingVideosRequest
import com.noljanolja.core.video.data.model.request.GetVideoDetailRequest
import com.noljanolja.core.video.data.model.request.GetVideosRequest
import com.noljanolja.core.video.domain.model.Comment
import com.noljanolja.core.video.domain.model.TrendingVideoDuration
import com.noljanolja.core.video.domain.model.Video
import com.noljanolja.core.video.domain.repository.VideoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch

internal class VideoRepositoryImpl(
    private val videoApi: VideoApi,
    private val localVideoDataSource: LocalVideoDataSource,
) : VideoRepository {

    private val scope = CoroutineScope(Dispatchers.Default)
    override fun getTrendingVideo(duration: TrendingVideoDuration): Flow<List<Video>> = flow {
        try {
            val videos =
                videoApi.getTrendingVideo(GetTrendingVideosRequest(duration = duration)).data.orEmpty()
            emit(videos)
            updateLocalVideos(videos)
        } catch (e: Throwable) {
            emit(emptyList())
        }
    }

    override fun getVideos(isHighlight: Boolean): Flow<List<Video>> = flow {
        try {
            val videos =
                videoApi.getVideos(GetVideosRequest(isHighlight = isHighlight)).data.orEmpty()
            emit(videos)
            updateLocalVideos(videos)
        } catch (e: Throwable) {
            emit(emptyList())
        }
    }

    override fun getWatchingVideos(): Flow<List<Video>> = flow {
        try {
            val videos =
                videoApi.getWatchingVideos().data.orEmpty()
            emit(videos)
            updateLocalVideos(videos)
        } catch (e: Throwable) {
            emit(emptyList())
        }
    }

    override suspend fun getVideoDetail(id: String): Flow<Video> {
        scope.launch {
            try {
                videoApi.getVideoDetail(GetVideoDetailRequest(videoId = id)).data?.let {
                    updateLocalVideo(it)
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
        return getLocalVideo(id)
    }

    override suspend fun commentVideo(videoId: String, comment: String): Result<Comment> {
        return try {
            val response = videoApi.commentVideo(videoId, CommentVideoRequest(comment))
            if (response.isSuccessful()) {
                localVideoDataSource.upsertVideoComments(videoId, listOf(response.data!!))
                localVideoDataSource.updateVideoCommentCount(videoId)
                Result.success(response.data!!)
            } else {
                throw Throwable("Comment video error")
            }
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    private suspend fun updateLocalVideo(video: Video) {
        localVideoDataSource.upsertCategory(video.category)
        localVideoDataSource.upsertChannel(video.channel)
        localVideoDataSource.upsertVideoComments(
            videoId = video.id,
            comments = video.comments
        )
        localVideoDataSource.upsert(video)
    }

    private suspend fun updateLocalVideos(videos: List<Video>) {
        videos.forEach {
            updateLocalVideo(it)
        }
    }

    private suspend fun getLocalVideo(videoId: String): Flow<Video> = with(localVideoDataSource) {
        findById(videoId).mapNotNull { it }.combine(findVideoComments(videoId)) { video, comments ->
            val channel = findChannelById(video.channel.id)
            val category = findCategoryById(video.category.id)
            video.copy(
                channel = channel ?: video.channel,
                category = category ?: video.category,
                comments = comments
            )
        }
    }
}