package com.noljanolja.android.util

import android.content.Context
import com.noljanolja.android.R
import java.text.NumberFormat
import kotlin.time.Duration.Companion.seconds

fun Int?.orZero() = this ?: 0
fun Long?.orZero() = this ?: 0L

fun Number.formatDigitsNumber(): String {
    val formatter = NumberFormat.getInstance()
    formatter.minimumFractionDigits = 0
    formatter.maximumFractionDigits = 0
    return formatter.format(this)
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

fun Long.formatTime(context: Context): String {
    val second = this / 1000
    return when {
        second < 60 -> context.getString(R.string.seconds, second)
        else -> context.getString(R.string.minutes, second / 60)
    }
}