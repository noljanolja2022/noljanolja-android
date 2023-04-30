package com.noljanolja.socket.di

import com.noljanolja.socket.SocketManager
import org.koin.dsl.module

val socketModule = module {
    single {
        SocketManager(get(), get(), get())
    }
}