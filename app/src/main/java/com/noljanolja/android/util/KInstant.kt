package com.noljanolja.android.util

import android.content.Context
import com.noljanolja.android.R
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.Instant
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.Year
import java.time.format.DateTimeFormatter
import java.util.Locale

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

fun Instant.formatMonthAndYear(): String {
    return this@formatMonthAndYear.customFormatTime("MMMM yyyy").capitalizeFirstLetter()
}

fun Instant.formatFullTime(): String {
    return this.customFormatTime("HH:mm - MMMM dd, yyyy").capitalizeLetterAt(8)
}

fun Instant.formatTransactionShortTime(): String {
    return this.customFormatTime("MMMM dd, yyyy").capitalizeFirstLetter()
}

fun Instant.getMonth() = this.toLocalDateTime(TimeZone.currentSystemDefault()).month.value

fun Instant.getYear() = this.toLocalDateTime(TimeZone.currentSystemDefault()).year

fun Instant.getDayOfMonth() = this.toLocalDateTime(TimeZone.currentSystemDefault()).dayOfMonth

fun Instant.getDayOfWeek() = this.toLocalDateTime(TimeZone.currentSystemDefault()).dayOfWeek

fun Int.getLastDay(year: Int) = Month.of(this).length(Year.of(year).isLeap)

private fun createInstantFromMonthYear(month: Int, year: Int): Instant {
    val m = if (month > 9) "$month" else "0$month"
    return Instant.parse("$year-$m-01T01:00:00Z")
}

fun formatMonthYear(month: Int, year: Int): String =
    createInstantFromMonthYear(month, year).customFormatTime("MMMM yyyy").capitalizeFirstLetter()