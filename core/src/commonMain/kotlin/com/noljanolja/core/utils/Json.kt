package com.noljanolja.core.utils

import kotlinx.serialization.json.Json

fun Json.default() = Json {
    prettyPrint = true
    isLenient = true
    ignoreUnknownKeys = true
    encodeDefaults = true
    coerceInputValues = true
}