package com.noljanolja.android.di

import android.content.Context
import com.noljanolja.android.R
import com.noljanolja.android.data.repositories.AuthRepository
import com.noljanolja.android.domain.repositories.AuthRepositoryImpl
import com.noljanolja.android.ui.screen.navigation.NavigationManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideNavigationManager() = NavigationManager()


    @Provides
    @Singleton
    fun provideAuthRepository(
        @ApplicationContext appContext: Context
    ): AuthRepository = AuthRepositoryImpl.getInstance(
        context = appContext,
        kakaoApiKey = appContext.getString(R.string.kakao_api_key),
        googleWebClientId = appContext.getString(R.string.web_client_id)
    )
}
