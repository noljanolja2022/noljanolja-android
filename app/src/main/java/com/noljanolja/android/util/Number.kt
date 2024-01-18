package com.noljanolja.android.util

import android.content.Context
import android.util.TypedValue
import com.noljanolja.android.R
import java.text.DecimalFormat

fun Int?.orZero() = this ?: 0
fun Long?.orZero() = this ?: 0L

fun Double.formatDouble(): String {
    val decimalFormat = DecimalFormat("#.##")
    return decimalFormat.format(this)
}

fun Number.formatDigitsNumber(): String {
    return try {
        val decimalFormat = DecimalFormat("#,###")
        decimalFormat.format(this)
    } catch (_: Exception) {
        ""
    }
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

fun Float.toDp(context: Context): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        context.resources.displayMetrics
    )
}