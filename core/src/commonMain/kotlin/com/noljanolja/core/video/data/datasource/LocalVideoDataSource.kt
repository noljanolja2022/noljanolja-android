package com.noljanolja.core.video.data.datasource

import com.noljanolija.core.db.CommentQueries
import com.noljanolija.core.db.VideoCategoryQueries
import com.noljanolija.core.db.VideoChannelQueries
import com.noljanolija.core.db.VideoQueries
import com.noljanolja.core.utils.transactionWithContext
import com.noljanolja.core.video.domain.model.Category
import com.noljanolja.core.video.domain.model.Channel
import com.noljanolja.core.video.domain.model.Comment
import com.noljanolja.core.video.domain.model.Commenter
import com.noljanolja.core.video.domain.model.Video
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

class LocalVideoDataSource(
    private val videoQueries: VideoQueries,
    private val videoCategoryQueries: VideoCategoryQueries,
    private val videoChannelQueries: VideoChannelQueries,
    private val commentQueries: CommentQueries,
    private val backgroundDispatcher: CoroutineDispatcher,
) {
    private val channelMapper = {
            id: String,
            title: String,
            thumbnail: String,
        ->
        Channel(
            id = id,
            title = title,
            thumbnail = thumbnail
        )
    }

    private val categoryMapper = {
            id: String,
            title: String,
        ->
        Category(
            id = id,
            title = title,
        )
    }

    private val videoMapper = {
            id: String,
            title: String,
            thumbnail: String,
            channelId: String,
            categoryId: String,
            viewCount: Long,
            commentCount: Long,
        ->
        Video(
            id = id,
            title = title,
            thumbnail = thumbnail,
            channel = Channel(id = channelId),
            category = Category(id = categoryId),
            viewCount = viewCount,
            commentCount = commentCount
        )
    }

    private val commentMapper = {
            id: Long,
            localId: String,
            video_id: String,
            comment: String,
            commenter_avatar: String,
            commenter_name: String,
            created_at: Long,
            updated_at: Long,
        ->
        Comment(
            id = id,
            _localId = localId,
            comment = comment,
            commenter = Commenter(
                avatar = commenter_avatar,
                name = commenter_name,
            )

        )
    }

    suspend fun findById(
        videoId: String,
    ): Flow<Video?> =
        videoQueries
            .findById(videoId, videoMapper)
            .asFlow()
            .mapToList()
            .map { it.firstOrNull() }
            .flowOn(backgroundDispatcher)

    suspend fun findChannelById(
        channelId: String,
    ): Channel? =
        videoChannelQueries
            .findById(channelId, channelMapper)
            .asFlow()
            .mapToOne()
            .flowOn(backgroundDispatcher)
            .firstOrNull()

    suspend fun findCategoryById(
        categoryId: String,
    ): Category? =
        videoCategoryQueries
            .findById(categoryId, categoryMapper)
            .asFlow()
            .mapToOne()
            .flowOn(backgroundDispatcher)
            .firstOrNull()

    suspend fun findVideoComments(
        videoId: String,
        limit: Long = 0,
    ): Flow<List<Comment>> = if (limit > 0) {
        commentQueries.findAllByVideo(videoId, commentMapper)
    } else {
        commentQueries.findAllByVideo(videoId, commentMapper)
    }.asFlow().mapToList().flowOn(backgroundDispatcher)

    suspend fun upsert(
        video: Video,
    ) = videoQueries.transactionWithContext(backgroundDispatcher) {
        videoQueries.upsert(
            id = video.id,
            title = video.title,
            thumbnail = video.thumbnail,
            category_id = video.category.id,
            channel_id = video.channel.id,
            view_count = video.viewCount,
            comment_count = video.commentCount
        )
    }

    suspend fun upsertChannel(
        channel: Channel,
    ) = videoChannelQueries.transactionWithContext(backgroundDispatcher) {
        videoChannelQueries.upsert(
            id = channel.id,
            title = channel.title,
            thumbnail = channel.thumbnail,
        )
    }

    suspend fun upsertCategory(
        category: Category,
    ) = videoCategoryQueries.transactionWithContext(backgroundDispatcher) {
        videoCategoryQueries.upsert(
            id = category.id,
            title = category.title,
        )
    }

    suspend fun upsertVideoComments(
        videoId: String,
        comments: List<Comment>,
    ) = commentQueries.transactionWithContext(backgroundDispatcher) {
        comments.forEach { comment ->
            val existing = if (comment.id != 0L) {
                commentQueries.findById(comment.id, commentMapper).executeAsOneOrNull()
            } else {
                null
            }
            commentQueries.upsert(
                id = comment.id,
                localId = existing?.localId ?: comment.localId,
                video_id = videoId,
                comment = comment.comment,
                commenter_avatar = comment.commenter.avatar,
                commenter_name = comment.commenter.name,
                created_at = Clock.System.now().toEpochMilliseconds(),
                updated_at = Clock.System.now().toEpochMilliseconds(),
            )
        }
    }

    suspend fun updateVideoCommentCount(
        videoId: String,
    ) = videoQueries.transactionWithContext(backgroundDispatcher) {
        videoQueries.updateCommentCount(videoId)
    }
}