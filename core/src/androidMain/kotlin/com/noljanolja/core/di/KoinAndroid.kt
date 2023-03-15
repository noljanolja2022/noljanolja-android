package com.noljanolja.core.di

import com.noljanolja.core.db.Noljanolja
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import io.ktor.client.engine.okhttp.*
import org.koin.core.module.Module
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
}