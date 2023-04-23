package com.noljanolja.core.video.domain.repository

import com.noljanolja.core.video.domain.model.Comment
import com.noljanolja.core.video.domain.model.TrendingVideoDuration
import com.noljanolja.core.video.domain.model.Video
import kotlinx.coroutines.flow.Flow

interface VideoRepository {
    fun getTrendingVideo(duration: TrendingVideoDuration): Flow<List<Video>>

    fun getVideos(isHighlight: Boolean): Flow<List<Video>>

    fun getWatchingVideos(): Flow<List<Video>>

    suspend fun getVideoDetail(id: String): Flow<Video>

    suspend fun commentVideo(videoId: String, comment: String): Result<Comment>
}