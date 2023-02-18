package com.noljanolja.android.services.analytics

sealed class Log(
    val level: String,
    val tag: String,
    val message: String,
    val error: Throwable?,
    val recordError: Boolean,
) {
    fun formattedLogMessage() = "$level/$tag:$message"

    class Verbose(
        tag: String,
        message: String,
        error: Throwable? = null,
        recordError: Boolean = false,
    ) : Log(
        level = "V",
        tag = tag,
        message = message,
        error = error,
        recordError = recordError,
    )

    class Debug(
        tag: String,
        message: String,
        error: Throwable? = null,
        recordError: Boolean = false,
    ) : Log(
        level = "D",
        tag = tag,
        message = message,
        error = error,
        recordError = recordError,
    )

    class Info(
        tag: String,
        message: String,
        error: Throwable? = null,
        recordError: Boolean = false,
    ) : Log(
        level = "I",
        tag = tag,
        message = message,
        error = error,
        recordError = recordError,
    )

    class Warn(
        tag: String,
        message: String,
        error: Throwable? = null,
        recordError: Boolean = false,
    ) : Log(
        level = "W",
        tag = tag,
        message = message,
        error = error,
        recordError = recordError,
    )

    class Error(
        tag: String,
        message: String,
        error: Throwable? = null,
        recordError: Boolean = true,
    ) : Log(
        level = "E",
        tag = tag,
        message = message,
        error = error,
        recordError = recordError,
    )

    class Assert(
        tag: String,
        message: String,
        error: Throwable? = null,
        recordError: Boolean = false,
    ) : Log(
        level = "A",
        tag = tag,
        message = message,
        error = error,
        recordError = recordError,
    )
}
