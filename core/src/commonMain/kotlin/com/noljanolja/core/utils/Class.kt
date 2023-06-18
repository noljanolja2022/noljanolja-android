package com.noljanolja.core.utils

fun <T> T?.takeOrDefault(default: T, predicate: (T) -> Boolean): T {
    return this?.takeIf(predicate) ?: default
}