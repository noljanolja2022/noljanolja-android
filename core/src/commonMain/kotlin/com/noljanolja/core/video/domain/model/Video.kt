package com.noljanolja.core.video.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Video(
    val category: Category = Category(),
    val channel: Channel = Channel(),
    val commentCount: Int = 0,
    val comments: List<Comment> = listOf(),
    val duration: String = "",
    val durationMs: Int = 0,
    val favoriteCount: Int = 0,
    val id: String = "",
    val isHighlighted: Boolean = false,
    val likeCount: Int = 0,
    val publishedAt: String = "",
    val thumbnail: String = "",
    val title: String = "",
    val url: String = "",
    val viewCount: Int = 0,
)

enum class TrendingVideoDuration {
    Day, Week, Month
}