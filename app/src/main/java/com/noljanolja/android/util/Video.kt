package com.noljanolja.android.util

import android.content.Context
import com.noljanolja.android.R
import com.noljanolja.core.video.domain.model.Video
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.Period
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun Video.getShortDescription(context: Context): String {
    return context.getString(
        R.string.video_detail_short_description,
        channel.title,
        viewCount.formatNumber(),
        publishedAt.parseLocalDateTime().formatPeriodVideo(context)
    )
}

fun LocalDateTime.formatPeriodVideo(context: Context): String {
    val now = LocalDateTime.now()
    val duration = Duration.between(this, now)
    val period = Period.between(this.toLocalDate(), now.toLocalDate())

    return when {
        period.years >= 1 -> context.getString(R.string.video_detail_duration_year, period.years)
        period.months >= 1 -> context.getString(R.string.video_detail_duration_month, period.months)
        period.days >= 1 -> context.getString(R.string.video_detail_duration_days, period.days)
        duration.toHours() >= 1 -> context.getString(R.string.video_detail_duration_hours, duration.toHours())
        duration.toMinutes() >= 1 -> context.getString(R.string.video_detail_duration_minutes, duration.toMinutes())
        else -> context.getString(R.string.video_detail_duration_just_now)
    }
}

fun String.parseLocalDateTime(): LocalDateTime {
    val formatter = DateTimeFormatter.ISO_INSTANT
    val instant = Instant.from(formatter.parse(this))
    return LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
}
