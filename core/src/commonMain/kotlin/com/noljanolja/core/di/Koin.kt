package com.noljanolja.core.di

import com.noljanolja.core.CoreManager
import com.noljanolja.core.auth.data.AuthRepositoryImpl
import com.noljanolja.core.auth.domain.repository.AuthRepository
import com.noljanolja.core.contacts.data.repository.ContactsRepositoryImpl
import com.noljanolja.core.contacts.domain.repository.ContactsRepository
import com.noljanolja.core.conversation.data.datasource.ConversationApi
import com.noljanolja.core.conversation.data.repository.ConversationRepositoryImpl
import com.noljanolja.core.conversation.domain.repository.ConversationRepository
import com.noljanolja.core.db.Noljanolja
import com.noljanolja.core.user.data.datasource.UserApi
import com.noljanolja.core.user.data.datasource.UserRemoteDataSource
import com.noljanolja.core.user.data.datasource.UserRemoteDataSourceImpl
import com.noljanolja.core.user.data.repository.UserRepositoryImpl
import com.noljanolja.core.user.domain.repository.UserRepository
import com.noljanolja.socket.di.socketModule
import kotlinx.coroutines.Dispatchers
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

fun initKoin(appModule: Module): KoinApplication {
    val koinApplication = startKoin {
        modules(
            appModule,
            platformModule,
            coreModule,
            socketModule,
        )
    }

    return koinApplication
}

expect val platformModule: Module

private val coreModule = module {

    single<UserRepository> {
        UserRepositoryImpl(get(), get(), get(), get())
    }
    single {
        UserApi(get())
    }
    single<UserRemoteDataSource> {
        UserRemoteDataSourceImpl(get())
    }
    single<ContactsRepository> {
        ContactsRepositoryImpl(get())
    }
    single {
        ConversationApi(
            get(),
            get(),
            get()
        )
    }
    single<ConversationRepository> {
        ConversationRepositoryImpl(get(), get())
    }
    single {
        Noljanolja(get())
    }
    single<AuthRepository> {
        AuthRepositoryImpl(get<Noljanolja>().authQueries, Dispatchers.Default)
    }
    single {
        CoreManager(get(), get(), get(), get())
    }
}