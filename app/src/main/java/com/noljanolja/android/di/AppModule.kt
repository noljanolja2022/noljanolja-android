package com.noljanolja.android.di

import android.content.Context
import com.d2brothers.firebase_auth.AuthSdk
import com.noljanolja.android.R
import com.noljanolja.android.common.navigation.NavigationManager
import com.noljanolja.android.common.user.data.repository.UserRepositoryImpl
import com.noljanolja.android.common.user.domain.repository.UserRepository
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
    fun provideUserRepository(): UserRepository = UserRepositoryImpl()

    @Provides
    @Singleton
    fun provideAuthSdk(@ApplicationContext appContext: Context): AuthSdk = AuthSdk.init(
        appContext,
        kakaoApiKey = appContext.getString(R.string.kakao_api_key),
        googleWebClientId = appContext.getString(R.string.web_client_id),
        naver_client_id = "3zDg6vMsJmoFk2TGOjcq",
        naver_client_secret = "8keRny2c_4",
        naver_client_name = "놀자놀자",
        region = "asia-northeast3",
    )
}
