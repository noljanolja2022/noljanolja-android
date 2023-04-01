package com.noljanolja.core.utils

fun <T : Comparable<T>> minOfNullable(a: T?, b: T?): T? {
    return when {
        b == null -> a
        a == null -> b
        else -> minOf(a, b)
    }
}