package com.noljanolja.android.services.analytics.firebase

import com.google.firebase.analytics.FirebaseAnalytics
import com.noljanolja.android.services.analytics.Event
import com.noljanolja.android.services.analytics.Tracker
import com.noljanolja.android.services.analytics.getDataAsBundle

class FirebaseTracker internal constructor(
    private val firebaseAnalytics: FirebaseAnalytics
) : Tracker {

    override var isEnable: Boolean = false

    override fun trackEvent(event: Event) {
        firebaseAnalytics.logEvent(event.name, event.getDataAsBundle())
    }
}
