package com.noljanolja.core

sealed class Failure(val code: Int) : Throwable() {
    object NotHasYoutubeChannel : Failure(400_002)
}
