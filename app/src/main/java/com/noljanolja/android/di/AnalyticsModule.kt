package com.noljanolja.android.di

import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.noljanolja.android.services.analytics.Analytics
import com.noljanolja.android.services.analytics.AppAnalytics
import com.noljanolja.android.services.analytics.firebase.FirebaseLogger
import com.noljanolja.android.services.analytics.firebase.FirebaseTracker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AnalyticsModule {

    @Singleton
    @Provides
    fun firebaseTracker(): FirebaseTracker = FirebaseTracker(
        Firebase.analytics,
    ).apply {
        // TODO: Should fetch from remote config or use BuildConfig
        isEnable = true
    }

    @Singleton
    @Provides
    fun firebaseLogger(): FirebaseLogger = FirebaseLogger(
        Firebase.crashlytics,
    ).apply {
        // TODO: Should fetch from remote config or use BuildConfig
        isEnable = true
    }

    @Singleton
    @Provides
    fun bindAnalytics(
        firebaseTracker: FirebaseTracker,
        firebaseLogger: FirebaseLogger,
    ): Analytics = AppAnalytics(
        trackers = mutableListOf(firebaseTracker),
        loggers = mutableListOf(firebaseLogger),
    )
}
