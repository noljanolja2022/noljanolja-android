package com.noljanolja.android.services.analytics.firebase

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.noljanolja.android.services.analytics.Log
import com.noljanolja.android.services.analytics.Logger

class FirebaseLogger internal constructor(
    private val firebaseCrashlytics: FirebaseCrashlytics
) : Logger {

    override var isEnable: Boolean = false

    override fun logMessage(log: Log) {
        firebaseCrashlytics.log(log.formattedLogMessage())
        if (log.recordError && log.error != null) {
            firebaseCrashlytics.recordException(log.error)
        }
    }
}
