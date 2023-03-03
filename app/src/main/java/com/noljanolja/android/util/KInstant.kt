package com.noljanolja.android.util

import kotlinx.datetime.*

fun Instant.humanReadableDate(): String {
    with(this.toLocalDateTime(TimeZone.currentSystemDefault())) {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

        return when {
            this.date == now.date -> "Today"
            this.date.plus(DatePeriod(days = 1)) == now.date -> "Yesterday"
            else -> "$month, $dayOfMonth"
        }
    }
}
