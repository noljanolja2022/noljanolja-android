package com.noljanolja.android.di

import android.os.Build
import com.d2brothers.firebase_auth.AuthSdk
import com.noljanolja.android.BuildConfig
import com.noljanolja.android.common.user.data.AuthDataSourceImpl
import com.noljanolja.core.CoreManager
import com.noljanolja.core.contacts.data.repository.ContactsRepositoryImpl
import com.noljanolja.core.contacts.domain.repository.ContactsRepository
import com.noljanolja.core.conversation.data.datasource.ConversationApi
import com.noljanolja.core.conversation.data.repository.ConversationRepositoryImpl
import com.noljanolja.core.conversation.domain.repository.ConversationRepository
import com.noljanolja.core.service.ktor.KtorClient
import com.noljanolja.core.service.ktor.KtorConfig
import com.noljanolja.core.user.data.datasource.AuthDataSource
import com.noljanolja.core.user.data.datasource.UserApi
import com.noljanolja.core.user.data.datasource.UserRemoteDataSource
import com.noljanolja.core.user.data.datasource.UserRemoteDataSourceImpl
import com.noljanolja.core.user.data.repository.UserRepositoryImpl
import com.noljanolja.core.user.domain.repository.UserRepository
import com.noljanolja.core.utils.Database
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class CoreModule {
    @Provides
    @Singleton
    fun provideAuthDataSource(authSdk: AuthSdk): AuthDataSource = AuthDataSourceImpl(authSdk)

    @Provides
    @Singleton
    fun provideUserRepository(
        dataSource: UserRemoteDataSource,
        authDataSource: AuthDataSource,
        client: HttpClient,
    ): UserRepository = UserRepositoryImpl(dataSource, authDataSource, client)

    @Provides
    @Singleton
    fun provideKtorConfig(authSdk: AuthSdk) = KtorConfig(
        userAgent = "noljanolja/${BuildConfig.VERSION_NAME} (Mobile; Android ${Build.VERSION.RELEASE}; ${Build.MANUFACTURER} ${Build.MODEL})"
    )

    @Provides
    @Singleton
    fun provideHttpClient(ktorConfig: KtorConfig, authSdk: AuthSdk): HttpClient =
        KtorClient.createInstance(
            OkHttp.create(),
            ktorConfig,
            getToken = {
                Database.saveToken(authSdk.getIdToken(false).orEmpty())
            },
            refreshToken = {
                Database.saveToken(authSdk.getIdToken(true).orEmpty())
            }
        )

    @Provides
    @Singleton
    fun provideUserApi(client: HttpClient): UserApi = UserApi(client)

    @Provides
    @Singleton
    fun provideUserRemoteDataSource(
        api: UserApi,
    ): UserRemoteDataSource = UserRemoteDataSourceImpl(api)

    @Provides
    @Singleton
    fun provideContactsRepository(
        userRemoteDataSource: UserRemoteDataSource,
    ): ContactsRepository = ContactsRepositoryImpl(userRemoteDataSource)

    @Provides
    @Singleton
    fun provideConversationApi(
        client: HttpClient,
        authSdk: AuthSdk,
    ) = ConversationApi(
        client,
        KtorClient.createRocketInstance(OkHttp.create())
    )

    @Provides
    @Singleton
    fun provideConversationRepository(
        conversationApi: ConversationApi,
        userRepository: UserRepository,
    ): ConversationRepository = ConversationRepositoryImpl(conversationApi, userRepository)

    @Provides
    @Singleton
    fun provideCoreManager(
        contactsRepository: ContactsRepository,
        userRepository: UserRepository,
        conversationRepository: ConversationRepository,
    ) = CoreManager(contactsRepository, userRepository, conversationRepository)
}