package com.noljanolja.android.services.analytics

interface Tracker {
    val isEnable: Boolean

    fun trackEvent(event: Event)
}

object NoOpTracker : Tracker {
    override val isEnable: Boolean
        get() = false

    override fun trackEvent(event: Event) {
        // DO Nothing
    }
}
