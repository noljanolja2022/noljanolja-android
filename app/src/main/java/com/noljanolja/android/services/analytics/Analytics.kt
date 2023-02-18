package com.noljanolja.android.services.analytics

import com.noljanolja.android.services.analytics.EventKey.USER_ID

interface Analytics {
    fun trackEvent(event: Event)
    fun logMessage(log: Log)
}

class AppAnalytics internal constructor(
    private val trackers: MutableList<Tracker>,
    private val loggers: MutableList<Logger>,
) : Analytics {

    override fun trackEvent(event: Event) {
        event.setGlobalParams(
            // TODO
            mapOf(
                USER_ID to "user",
            ),
        )
        trackers.forEach { tracker ->
            if (tracker.isEnable) tracker.trackEvent(event)
        }
    }

    override fun logMessage(log: Log) {
        loggers.forEach { logger ->
            if (logger.isEnable) logger.logMessage(log)
        }
    }
}
