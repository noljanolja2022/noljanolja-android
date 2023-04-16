package com.noljanolja.android.util

import android.content.Context
import com.noljanolja.android.R
import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import java.time.format.DateTimeFormatter
import java.util.*

fun Instant.humanReadableDate(): String {
    with(this.toLocalDateTime(TimeZone.currentSystemDefault())) {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

        return when {
            this.date == now.date -> {
                this@humanReadableDate.customFormatTime("hh:mm a")
            }

            this.date.plus(DatePeriod(days = 1)) == now.date -> "Yesterday"
            else -> "$dayOfMonth/$monthNumber/$year"
        }
    }
}

fun Instant.chatMessageHeaderDate(context: Context): String {
    with(this.toLocalDateTime(TimeZone.currentSystemDefault())) {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM, yyyy")
            .withLocale(Locale.ENGLISH)

        return when {
            this.date == now.date -> context.getString(R.string.common_today)
            this.date.plus(DatePeriod(days = 1)) == now.date -> context.getString(R.string.common_yesterday)
            else -> this.date.toJavaLocalDate().format(formatter)
        }
    }
}

fun Instant.humanReadableTime(): String {
    with(this.toLocalDateTime(TimeZone.currentSystemDefault())) {
        return "$hour:$minute"
    }
}

fun Instant.chatMessageBubbleTime(): String {
    return this.customFormatTime("hh:mm")
}

fun Instant.isSameDate(other: Instant): Boolean {
    with(this.toLocalDateTime(TimeZone.currentSystemDefault())) {
        val otherLocalDateTime = other.toLocalDateTime(TimeZone.currentSystemDefault())

        return this.date == otherLocalDateTime.date
    }
}

fun Instant.customFormatTime(format: String): String {
    return DateTimeFormatter.ofPattern(format)
        .format(this.toLocalDateTime(TimeZone.currentSystemDefault()).toJavaLocalDateTime())
}
