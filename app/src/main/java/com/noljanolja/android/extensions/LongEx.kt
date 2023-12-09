package com.noljanolja.android.extensions

/**
 * Created by tuyen.dang on 11/14/2023.
 */

fun Long?.convertToLong(defaultValue: Long = 0L) = this ?: defaultValue
