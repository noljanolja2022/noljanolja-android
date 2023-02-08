package com.noljanolja.android.services.analytics

import android.util.Log as AndroidLogger

interface Logger {
    val isEnable: Boolean

    fun logMessage(log: Log)
}

object DefaultLogger : Logger {
    override val isEnable: Boolean
        get() = true

    override fun logMessage(log: Log) {
        when (log) {
            is Log.Verbose -> AndroidLogger.v(log.tag, log.message, log.error)
            is Log.Debug -> AndroidLogger.d(log.tag, log.message, log.error)
            is Log.Info -> AndroidLogger.i(log.tag, log.message, log.error)
            is Log.Warn -> AndroidLogger.w(log.tag, log.message, log.error)
            is Log.Error -> AndroidLogger.e(log.tag, log.message, log.error)
            is Log.Assert -> AndroidLogger.wtf(log.tag, log.message, log.error)
        }
    }
}
