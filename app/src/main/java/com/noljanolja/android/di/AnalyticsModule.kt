package com.noljanolja.android.di

import android.content.Context
import com.noljanolja.android.data.repositories.AnalyticsRepoImpl
import com.noljanolja.android.domain.repositories.AnalyticsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class AnalyticsModule {
    @Binds
    fun bindAnalyticsRepository(@ApplicationContext context: Context): AnalyticsRepository =
        AnalyticsRepoImpl(context)
}