package com.noljanolja.android.extensions

/**
 * Created by tuyen.dang on 12/17/2023.
 */

internal inline fun <reified T> Any?.castTo(): T? = this as? T
