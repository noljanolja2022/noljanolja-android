package com.noljanolja.core.video.domain.model

import kotlinx.serialization.Serializable
import kotlin.math.roundToInt

@Serializable
data class Video(
    val category: Category = Category(),
    val channel: Channel = Channel(),
    val commentCount: Long = 0,
    val inAppCommentCount: Long = 0,
    val comments: List<Comment> = listOf(),
    val duration: String = "",
    val durationMs: Long = 0,
    val currentProgressMs: Long = 0,
    val progressPercentage: Float = 0F,
    val favoriteCount: Long = 0,
    val id: String = "", //
    val isHighlighted: Boolean = false,
    val likeCount: Long = 0,
    val isLiked: Boolean = false,
    val publishedAt: String = "",
    val thumbnail: String = "", //
    val title: String = "", //
    val url: String = "",
    val viewCount: Long = 0,
    val earnedPoints: Long = 0,
    val totalPoints: Long = 0,
) {
    fun getVideoProgress(): Float {
        return currentProgressMs.toFloat() / durationMs.toFloat()
    }

    fun getVideoPercentProgress(): Int = (getVideoProgress() * 100).roundToInt()
}

enum class TrendingVideoDuration {
    Day, Week, Month
}