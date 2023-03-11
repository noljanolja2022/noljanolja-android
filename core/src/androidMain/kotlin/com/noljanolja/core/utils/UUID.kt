package com.noljanolja.core.utils

import java.util.*

internal actual fun randomUUID(): String {
    return UUID.randomUUID().toString()
}