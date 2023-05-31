package com.noljanolja.core.di

import com.noljanolja.core.db.Noljanolja
import com.noljanolja.socket.TokenRepo
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import io.ktor.client.engine.okhttp.*
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual val platformModule: Module = module {
    single {
        OkHttp.create {
            config {
                retryOnConnectionFailure(true)
            }
        }
    }
    single<SqlDriver> {
        AndroidSqliteDriver(Noljanolja.Schema, get(), "Noljanolja")
    }
    single(named("Coil")) {
        val authRepo = get<TokenRepo>()
        Interceptor { chain ->
            val request = chain.request()
            chain.proceed(
                request.newBuilder()
                    .apply {
                        runBlocking { authRepo.getToken() }?.let {
                            if (!request.url.toString().contains("googleapis")) {
                                header("Authorization", "Bearer $it")
                            }
                        }
                    }
                    .build()
            )
        }
    }
    single(named("Coil")) {
        OkHttpClient.Builder()
            .addInterceptor(get<Interceptor>(named("Coil")))
            .build()
    }
}