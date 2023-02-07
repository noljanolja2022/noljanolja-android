package com.noljanolja.android.di

import android.content.Context
import com.noljanolja.android.common.data.repositories.AnalyticsRepoImpl
import com.noljanolja.android.common.domain.repositories.AnalyticsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class AnalyticsModule {
    @Provides
    fun bindAnalyticsRepository(@ApplicationContext context: Context): AnalyticsRepository =
        AnalyticsRepoImpl(context)
}