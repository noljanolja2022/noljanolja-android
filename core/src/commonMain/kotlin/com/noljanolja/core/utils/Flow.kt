package com.noljanolja.core.utils

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flow

fun <T> Flow<T>.throttle(periodMillis: (T) -> Long): Flow<T> {
    return flow {
        conflate().collect { value ->
            emit(value)
            delay(periodMillis.invoke(value))
        }
    }
}