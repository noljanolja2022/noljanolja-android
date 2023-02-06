package com.noljanolja.android.data.repositories

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.noljanolja.android.BuildConfig
import com.noljanolja.android.domain.repositories.AnalyticsRepository
import com.noljanolja.android.domain.repositories.DestinationSdkType

class AnalyticsRepoImpl(private val context: Context) : AnalyticsRepository {
    private val firebaseAnalytics: FirebaseAnalytics by lazy { FirebaseAnalytics.getInstance(context) }
    override fun sendEvent(
        eventName: String,
        params: MutableMap<String, Any>?,
        destinationSdk: DestinationSdkType
    ) {
        setGlobalParams(params)
        when (destinationSdk) {
            DestinationSdkType.FIREBASE -> {
                logEventToFirebase(eventName)
            }
        }
    }

    private fun setGlobalParams(params: MutableMap<String, Any>?) {
        params?.let {
            params[AnalyticsRepository.APP_VERSION_NAME] = BuildConfig.VERSION_NAME
            params[AnalyticsRepository.APP_VERSION_CODE] = BuildConfig.VERSION_CODE
            params[AnalyticsRepository.USER_ID] = "user_id" // TODO
        }
    }

    private fun logEventToFirebase(
        eventName: String,
        eventsMap: MutableMap<String, Any>? = null
    ) {
        val bundle = Bundle().also { bundle ->
            eventsMap?.let {
                for (item in it.keys) {
                    bundle.putString(item, it[item].toString())
                }
            }
        }
        firebaseAnalytics.logEvent(eventName, bundle)
    }
}