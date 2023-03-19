package com.noljanolja.core.service.ktor

import com.noljanolja.core.CoreManager
import com.noljanolja.core.utils.default
import com.noljanolja.socket.TokenRepo
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.observer.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import co.touchlab.kermit.Logger as KLogger
import kotlinx.serialization.json.Json as KJson

object KtorClient {
    fun createInstance(
        engine: HttpClientEngine,
        config: KtorConfig,
        coreManager: CoreManager,
        tokenRepo: TokenRepo,
    ) = HttpClient(engine) {
        install(ContentNegotiation) {
            json(
                KJson.default()
            )
        }
        install(Auth) {
            bearer {
                loadTokens {
                    coreManager.getAuthToken()?.let {
                        BearerTokens(it, it)
                    }
                }
                refreshTokens {
                    if (response.status == HttpStatusCode.Unauthorized) {
                        tokenRepo.refreshToken()
                        coreManager.getAuthToken()?.let {
                            BearerTokens(it, it)
                        }
                    } else {
                        null
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