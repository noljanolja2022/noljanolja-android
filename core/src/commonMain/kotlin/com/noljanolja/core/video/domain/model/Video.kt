package com.noljanolja.core.video.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Video(
    val category: Category = Category(),
    val channel: Channel = Channel(),
    val commentCount: Long = 0,
    val comments: List<Comment> = listOf(),
    val duration: String = "",
    val durationMs: Long = 0,
    val favoriteCount: Long = 0,
    val id: String = "", //
    val isHighlighted: Boolean = false,
    val likeCount: Long = 0,
    val publishedAt: String = "",
    val thumbnail: String = "", //
    val title: String = "", //
    val url: String = "",
    val viewCount: Long = 0,
)

enum class TrendingVideoDuration {
    Day, Week, Month
}