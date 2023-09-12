package com.noljanolja.core.video.data.repository

import com.noljanolja.core.Failure
import com.noljanolja.core.shop.data.datasource.SearchLocalDatasource
import com.noljanolja.core.shop.domain.model.SearchKey
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

internal class VideoRepositoryImpl(
    private val videoApi: VideoApi,
    private val localVideoDataSource: LocalVideoDataSource,
    private val searchLocalDatasource: SearchLocalDatasource,
) : VideoRepository {

    private val scope = CoroutineScope(Dispatchers.Default)
    override fun getTrendingVideo(duration: TrendingVideoDuration): Flow<List<Video>> = flow {
        try {
            val videos =
                videoApi.getTrendingVideo(GetTrendingVideosRequest(duration = duration)).data.orEmpty()
            emit(videos)
//            updateLocalVideos(videos)
        } catch (e: Throwable) {
            emit(emptyList())
        }
    }

    override fun getVideos(isHighlight: Boolean?, query: String?): Flow<List<Video>> = flow {
        try {
            query?.let {
                searchLocalDatasource.insertKey(it, screen = SCREEN)
            }
            val videos =
                videoApi.getVideos(
                    GetVideosRequest(
                        isHighlight = isHighlight,
                        query = query
                    )
                ).data.orEmpty()
            emit(videos)
//            updateLocalVideos(videos)
        } catch (e: Throwable) {
            emit(emptyList())
        }
    }

    override fun getWatchingVideos(): Flow<List<Video>> = flow {
        try {
            val videos =
                videoApi.getWatchingVideos().data.orEmpty()
            emit(videos)
//            updateLocalVideos(videos)
        } catch (e: Throwable) {
            emit(emptyList())
        }
    }

    override suspend fun getPromotedVideos(): Result<List<Video>> {
        return try {
            val result = videoApi.getPromotedVideo()
            if (result.isSuccessful()) {
                Result.success(result.data.orEmpty())
            } else {
                throw Throwable(result.message)
            }
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    override suspend fun getVideoDetail(id: String): Flow<Video> = flow {
        try {
            videoApi.getVideoDetail(GetVideoDetailRequest(videoId = id)).data?.let {
                emit(it)
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    override suspend fun commentVideo(
        videoId: String,
        comment: String,
        youtubeToken: String,
    ): Result<Comment> {
        return try {
            val response =
                videoApi.commentVideo(videoId, CommentVideoRequest(comment, "$youtubeToken"))
            if (response.isSuccessful()) {
                localVideoDataSource.upsertVideoComments(videoId, listOf(response.data!!))
                localVideoDataSource.updateVideoCommentCount(videoId)
                Result.success(response.data)
            } else if (response.code == Failure.NotHasYoutubeChannel.code) {
                throw Failure.NotHasYoutubeChannel
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

    override suspend fun clearSearchHistories() {
        searchLocalDatasource.deleteByScreen(SCREEN)
    }

    override suspend fun clearSearchText(text: String) {
        searchLocalDatasource.deleteByText(text, screen = SCREEN)
    }

    override fun getSearchVideoHistories(): Flow<List<SearchKey>> {
        return searchLocalDatasource.findAllByScreen(SCREEN)
    }

    companion object {
        private const val SCREEN = "VIDEO"
    }
}