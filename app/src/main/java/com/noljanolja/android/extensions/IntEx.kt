package com.noljanolja.android.extensions

import android.content.*
import com.noljanolja.android.*
import com.noljanolja.android.util.*

/**
 * Created by tuyen.dang on 12/16/2023.
 */

fun Int?.convertToInt(defaultValue: Int = 0) = this ?: defaultValue

internal fun Context.getLabelMinutes(value: Int): String {
    return getString(
        if (value > 1) R.string.labelMinutes else R.string.labelMinute, value
    )
}

internal fun Context.getLabelHours(value: Int): String {
    return getString(
        if (value > 1) R.string.labelHours else R.string.labelHour, value
    )
}

fun Context.getDistanceTimeDisplay(distanceTime: Long): String {
    var tempDistance = distanceTime
    var result = ""
    val hour = (tempDistance / Constant.Timer.ONE_HOUR).toInt()
    tempDistance %= Constant.Timer.ONE_HOUR
    if (hour > 0) {
        result += " ${getLabelHours(hour)}"
    }
    val minute = (tempDistance / Constant.Timer.MILLISECOND_OF_ONE_MINUTE).toInt()
    tempDistance %= Constant.Timer.MILLISECOND_OF_ONE_MINUTE
    if (minute > 0) {
        result += " ${getLabelMinutes(minute)}"
    }
    return result
}
 