package com.noljanolja.core.service.ktor

import com.noljanolja.core.auth.domain.repository.AuthRepository
import com.noljanolja.core.utils.default
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.observer.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import co.touchlab.kermit.Logger as KLogger
import kotlinx.serialization.json.Json as KJson

object KtorClient {
    fun createInstance(
        engine: HttpClientEngine,
        config: KtorConfig,
        authRepository: AuthRepository,
        refreshToken: suspend () -> Unit,
    ) = HttpClient(engine) {
        install(ContentNegotiation) {
            json(
                KJson.default()
            )
        }
        install(Auth) {
            bearer {
                loadTokens {
                    authRepository.getAuthToken()?.let {
                        BearerTokens(it, it)
                    }
                }
                refreshTokens {
                    refreshToken.invoke()
                    authRepository.getAuthToken()?.let {
                        BearerTokens(it, it)
                    }
                }
            }
        }

        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    KLogger.d("Logger Ktor => $message")
                }
            }
            level = LogLevel.ALL
        }

        install(ResponseObserver) {
            onResponse { response ->
                KLogger.d {
                    "HTTP status: ${response.status.value}"
                }
            }
        }

        install(DefaultRequest) {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            header(HttpHeaders.UserAgent, config.userAgent)
        }
    }
}

data class KtorConfig(
    val userAgent: String,
)