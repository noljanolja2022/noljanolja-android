package com.noljanolja.android.di

import android.content.Context
import com.d2brothers.firebase_auth.AuthSdk
import com.noljanolja.android.R
import com.noljanolja.android.common.contact.data.ContactsLoader
import com.noljanolja.android.common.navigation.NavigationManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideNavigationManager() = NavigationManager()

    @Provides
    @Singleton
    fun provideAuthSdk(@ApplicationContext appContext: Context): AuthSdk = AuthSdk.init(
        context = appContext,
        kakaoApiKey = appContext.getString(R.string.kakao_api_key),
        googleWebClientId = appContext.getString(R.string.web_client_id),
        naverClientId = "3zDg6vMsJmoFk2TGOjcq",
        naverClientSecret = "8keRny2c_4",
        naverClientName = "놀자놀자",
        region = "asia-northeast3",
    )

    @Provides
    @Singleton
    fun provideContactsLoader(
        @ApplicationContext appContext: Context,
    ): ContactsLoader = ContactsLoader(appContext)
}
