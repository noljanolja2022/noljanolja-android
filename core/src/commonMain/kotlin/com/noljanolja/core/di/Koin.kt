package com.noljanolja.core.di

import com.noljanolja.core.ClearManager
import com.noljanolja.core.CoreManager
import com.noljanolja.core.auth.data.AuthRepositoryImpl
import com.noljanolja.core.auth.domain.repository.AuthRepository
import com.noljanolja.core.contacts.data.repository.ContactsRepositoryImpl
import com.noljanolja.core.contacts.domain.repository.ContactsRepository
import com.noljanolja.core.conversation.data.datasource.ConversationApi
import com.noljanolja.core.conversation.data.datasource.LocalConversationDataSource
import com.noljanolja.core.conversation.data.datasource.LocalReactDataSource
import com.noljanolja.core.conversation.data.repository.ConversationRepositoryImpl
import com.noljanolja.core.conversation.domain.repository.ConversationRepository
import com.noljanolja.core.db.Noljanolja
import com.noljanolja.core.event.data.datasource.BannerApi
import com.noljanolja.core.event.data.repository.EventBannerRepositoryImpl
import com.noljanolja.core.event.domain.repository.EventBannerRepository
import com.noljanolja.core.loyalty.data.datasource.LoyaltyApi
import com.noljanolja.core.loyalty.data.repository.LoyaltyRepositoryImpl
import com.noljanolja.core.loyalty.domain.repository.LoyaltyRepository
import com.noljanolja.core.media.data.datasource.MediaApi
import com.noljanolja.core.media.data.repository.MediaRepositoryImpl
import com.noljanolja.core.media.domain.repository.MediaRepository
import com.noljanolja.core.shop.data.datasource.SearchLocalDatasource
import com.noljanolja.core.shop.data.datasource.ShopApi
import com.noljanolja.core.shop.data.repository.ShopRepositoryImpl
import com.noljanolja.core.shop.domain.repository.ShopRepository
import com.noljanolja.core.user.data.datasource.LocalUserDataSource
import com.noljanolja.core.user.data.datasource.UserApi
import com.noljanolja.core.user.data.datasource.UserRemoteDataSource
import com.noljanolja.core.user.data.datasource.UserRemoteDataSourceImpl
import com.noljanolja.core.user.data.repository.UserRepositoryImpl
import com.noljanolja.core.user.domain.repository.UserRepository
import com.noljanolja.core.video.data.datasource.LocalVideoDataSource
import com.noljanolja.core.video.data.datasource.VideoApi
import com.noljanolja.core.video.data.repository.VideoRepositoryImpl
import com.noljanolja.core.video.domain.repository.VideoRepository
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
    single { ClearManager(get<Noljanolja>(), get()) }
    single {
        with(get<Noljanolja>()) {
            SearchLocalDatasource(searchTextQueries, Dispatchers.Default)
        }
    }
    single {
        with(get<Noljanolja>()) {
            LocalUserDataSource(
                userQueries,
                participantQueries,
                memberInfoQueries,
                Dispatchers.Default
            )
        }
    }
    single {
        with(get<Noljanolja>()) {
            LocalReactDataSource(
                reactQueries,
                Dispatchers.Default
            )
        }
    }
    single {
        with(get<Noljanolja>()) {
            LocalVideoDataSource(
                videoQueries,
                videoCategoryQueries,
                videoChannelQueries,
                commentQueries,
                Dispatchers.Default
            )
        }
    }
    single {
        with(get<Noljanolja>()) {
            LocalConversationDataSource(
                conversationQueries,
                messageQueries,
                participantQueries,
                Dispatchers.Default
            )
        }
    }
    single {
        ShopApi(get())
    }
    single<ShopRepository> {
        ShopRepositoryImpl(get(), get())
    }

    single<UserRepository> {
        UserRepositoryImpl(get(), get(), get(), get(), get(), get())
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
        )
    }
    single<ConversationRepository> {
        ConversationRepositoryImpl(get(), get(), get(), get(), get())
    }
    single {
        MediaApi(get())
    }
    single<MediaRepository> {
        MediaRepositoryImpl(get())
    }
    single {
        VideoApi(get())
    }
    single<VideoRepository> {
        VideoRepositoryImpl(get(), get(), get())
    }
    single {
        LoyaltyApi(get())
    }
    single<LoyaltyRepository> {
        LoyaltyRepositoryImpl(get(), get())
    }
    single {
        Noljanolja(get())
    }
    single<AuthRepository> {
        AuthRepositoryImpl(get<Noljanolja>().authQueries, Dispatchers.Default)
    }
    single {
        BannerApi(get())
    }
    single<EventBannerRepository> {
        EventBannerRepositoryImpl(get())
    }
    single {
        CoreManager()
    }
}