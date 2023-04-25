package com.noljanolja.android.util

import com.noljanolja.core.video.domain.model.Video
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.Period
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun Video.getShortDescription(): String {
    return "${channel.title}  · ${viewCount.formatNumber()} views · ${
        publishedAt.parseLocalDateTime().formatPeriodVideo()
    }"
}

fun Long.formatNumber(): String {
    val number = this.toDouble()
    return when {
        number < 1000 -> String.format("%.0f", number)
        number < 10000 -> String.format("%.1f", number / 1000) + "k"
        number < 1000000 -> String.format("%.0f", number / 1000) + "k"
        else -> String.format("%.1f", number / 1000000) + "M"
    }
}

fun LocalDateTime.formatPeriodVideo(): String {
    val now = LocalDateTime.now()
    val duration = Duration.between(this, now)
    val period = Period.between(this.toLocalDate(), now.toLocalDate())

    return when {
        period.years >= 1 -> "${period.years} years ago"
        period.months >= 1 -> "${period.months} months ago"
        period.days >= 1 -> "${period.days} days ago"
        duration.toHours() >= 1 -> "${duration.toHours()} hours ago"
        duration.toMinutes() >= 1 -> "${duration.toMinutes()} minutes ago"
        else -> "just now"
    }
}

fun String.parseLocalDateTime(): LocalDateTime {
    val formatter = DateTimeFormatter.ISO_INSTANT
    val instant = Instant.from(formatter.parse(this))
    return LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
}
