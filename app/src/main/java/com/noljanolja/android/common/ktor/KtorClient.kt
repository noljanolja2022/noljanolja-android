package com.noljanolja.android.common.ktor

import android.os.Build
import android.util.Log
import com.d2brothers.firebase_auth.AuthSdk
import com.noljanolja.android.BuildConfig
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
import kotlinx.serialization.json.Json as KJson

object KtorClient {
    private const val TIME_OUT = 30_000L
    fun createInstance(authSdk: AuthSdk) = HttpClient(Android) {
        install(ContentNegotiation) {
            json(
                KJson {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                }
            )
        }
        install(Auth) {
            bearer {
                loadTokens {
                    getToken(authSdk)
                }
                refreshTokens {
                    getToken(authSdk)
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
            header(
                HttpHeaders.UserAgent,
                "noljanolja/${BuildConfig.VERSION_NAME} (Mobile; Android ${Build.VERSION.RELEASE}; ${Build.MANUFACTURER} ${Build.MODEL})"
            )
        }
    }
}

private suspend fun getToken(authSdk: AuthSdk): BearerTokens? {
    val token = authSdk.getIdToken(false) ?: authSdk.getIdToken(true)
    return token?.let {
        BearerTokens(it, it)
    }
}