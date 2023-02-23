package com.noljanolja.android.common.ktor

import android.util.Log
import com.d2brothers.firebase_auth.AuthSdk
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.observer.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*

object KtorClient {
    private const val TIME_OUT = 30_000L
    fun createInstance() = HttpClient(Android) {
        install(ContentNegotiation) {
            json(
                kotlinx.serialization.json.Json {
                    prettyPrint = true
                    isLenient = true
                }
            )
        }
        install(Auth) {
            bearer {
                loadTokens {
                    AuthSdk.instance.getIdToken(false)?.let {
                        BearerTokens(it, it)
                    }
                }
            }
        }

        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Log.v("Logger Ktor =>", message)
                }
            }
            level = LogLevel.ALL
        }

        install(ResponseObserver) {
            onResponse { response ->
                Log.d("HTTP status:", "${response.status.value}")
            }
        }

        install(DefaultRequest) {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
        }
    }

}
