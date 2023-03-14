package com.noljanolja.android.di

import android.content.Context
import android.os.Build
import com.d2brothers.firebase_auth.AuthSdk
import com.noljanolja.android.BuildConfig
import com.noljanolja.android.common.user.data.AuthDataSourceImpl
import com.noljanolja.core.CoreManager
import com.noljanolja.core.auth.data.AuthRepositoryImpl
import com.noljanolja.core.auth.domain.repository.AuthRepository
import com.noljanolja.core.contacts.data.repository.ContactsRepositoryImpl
import com.noljanolja.core.contacts.domain.repository.ContactsRepository
import com.noljanolja.core.conversation.data.datasource.ConversationApi
import com.noljanolja.core.conversation.data.repository.ConversationRepositoryImpl
import com.noljanolja.core.conversation.domain.repository.ConversationRepository
import com.noljanolja.core.db.Noljanolja
import com.noljanolja.core.service.ktor.KtorClient
import com.noljanolja.core.service.ktor.KtorConfig
import com.noljanolja.core.user.data.datasource.AuthDataSource
import com.noljanolja.core.user.data.datasource.UserApi
import com.noljanolja.core.user.data.datasource.UserRemoteDataSource
import com.noljanolja.core.user.data.datasource.UserRemoteDataSourceImpl
import com.noljanolja.core.user.data.repository.UserRepositoryImpl
import com.noljanolja.core.user.domain.repository.UserRepository
import com.noljanolja.core.utils.DriverFactory
import com.squareup.sqldelight.db.SqlDriver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import kotlinx.coroutines.Dispatchers
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
        authRepository: AuthRepository,
    ): UserRepository = UserRepositoryImpl(dataSource, authDataSource, client, authRepository)

    @Provides
    @Singleton
    fun provideKtorConfig(authSdk: AuthSdk) = KtorConfig(
        userAgent = "noljanolja/${BuildConfig.VERSION_NAME} (Mobile; Android ${Build.VERSION.RELEASE}; ${Build.MANUFACTURER} ${Build.MODEL})"
    )

    @Provides
    @Singleton
    fun provideHttpClient(
        ktorConfig: KtorConfig,
        authSdk: AuthSdk,
        authRepository: AuthRepository,
    ): HttpClient =
        KtorClient.createInstance(
            OkHttp.create(),
            ktorConfig,
            authRepository,
            refreshToken = {
                authRepository.saveAuthToken(authSdk.getIdToken(true).orEmpty())
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
        authRepository: AuthRepository,
    ) = ConversationApi(
        client,
        KtorClient.createRocketInstance(OkHttp.create()),
        authRepository
    )

    @Provides
    @Singleton
    fun provideConversationRepository(
        conversationApi: ConversationApi,
        userRepository: UserRepository,
    ): ConversationRepository = ConversationRepositoryImpl(conversationApi, userRepository)

    @Provides
    @Singleton
    fun provideSqlDriver(@ApplicationContext appContext: Context): SqlDriver =
        DriverFactory(appContext).createDriver()

    @Provides
    @Singleton
    fun provideDatabase(sqlDriver: SqlDriver): Noljanolja = Noljanolja(sqlDriver)

    @Provides
    @Singleton
    fun provideAuthRepository(
        database: Noljanolja,
    ): AuthRepository = AuthRepositoryImpl(database.authQueries, Dispatchers.Default)

    @Provides
    @Singleton
    fun provideCoreManager(
        contactsRepository: ContactsRepository,
        userRepository: UserRepository,
        conversationRepository: ConversationRepository,
        authRepository: AuthRepository,
    ) = CoreManager(contactsRepository, userRepository, conversationRepository, authRepository)
}