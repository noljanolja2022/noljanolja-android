package com.noljanolja.android.di

import android.content.Context
import com.d2brothers.firebase_auth.AuthSdk
import com.noljanolja.android.R
import com.noljanolja.android.common.ktor.KtorClient
import com.noljanolja.android.common.navigation.NavigationManager
import com.noljanolja.android.common.user.data.datasource.UserApi
import com.noljanolja.android.common.user.data.datasource.UserRemoteDataSource
import com.noljanolja.android.common.user.data.datasource.UserRemoteDataSourceImpl
import com.noljanolja.android.common.user.data.repository.UserRepositoryImpl
import com.noljanolja.android.common.user.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideNavigationManager() = NavigationManager()

    @Provides
    @Singleton
    fun provideUserRepository(
        dataSource: UserRemoteDataSource,
        authSdk: AuthSdk,
    ): UserRepository = UserRepositoryImpl(dataSource, authSdk)

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
    fun provideHttpClient(authSdk: AuthSdk): HttpClient = KtorClient.createInstance(authSdk)

    @Provides
    @Singleton
    fun provideUserApi(client: HttpClient): UserApi = UserApi(client)

    @Provides
    @Singleton
    fun provideUserRemoteDataSource(
        api: UserApi,
    ): UserRemoteDataSource = UserRemoteDataSourceImpl(api)
}
