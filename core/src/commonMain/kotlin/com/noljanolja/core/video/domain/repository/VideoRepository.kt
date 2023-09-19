package com.noljanolja.core.video.domain.repository

import com.noljanolja.core.shop.domain.model.SearchKey
import com.noljanolja.core.video.domain.model.Comment
import com.noljanolja.core.video.domain.model.PromotedVideo
import com.noljanolja.core.video.domain.model.TrendingVideoDuration
import com.noljanolja.core.video.domain.model.Video
import kotlinx.coroutines.flow.Flow

interface VideoRepository {
    fun getTrendingVideo(duration: TrendingVideoDuration): Flow<List<Video>>

    fun getVideos(isHighlight: Boolean?, query: String?): Flow<List<Video>>

    fun getWatchingVideos(): Flow<List<Video>>

    suspend fun getPromotedVideos(): Result<List<PromotedVideo>>

    suspend fun getVideoDetail(id: String): Flow<Video>

    suspend fun commentVideo(
        videoId: String,
        comment: String,
        youtubeToken: String,
    ): Result<Comment>

    suspend fun likeVideo(
        videoId: String,
        youtubeToken: String,
    ): Result<Boolean>

    suspend fun subscribeChannel(
        channelId: String,
        youtubeToken: String,
    ): Result<Boolean>
    suspend fun clearSearchHistories()

    suspend fun clearSearchText(text: String)

    fun getSearchVideoHistories(): Flow<List<SearchKey>>
}