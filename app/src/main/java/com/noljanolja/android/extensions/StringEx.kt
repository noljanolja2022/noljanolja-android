package com.noljanolja.android.extensions

/**
 * Created by tuyen.dang on 11/14/2023.
 */
 
fun String?.convertToString(defaultValue: String = "") = this ?: defaultValue
