package com.noljanolja.socket

import co.touchlab.kermit.Logger
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.observer.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.rsocket.kotlin.ExperimentalMetadataApi
import io.rsocket.kotlin.RSocket
import io.rsocket.kotlin.core.RSocketConnector
import io.rsocket.kotlin.core.WellKnownMimeType
import io.rsocket.kotlin.keepalive.KeepAlive
import io.rsocket.kotlin.ktor.client.RSocketSupport
import io.rsocket.kotlin.ktor.client.rSocket
import io.rsocket.kotlin.metadata.CompositeMetadata
import io.rsocket.kotlin.metadata.RoutingMetadata
import io.rsocket.kotlin.metadata.metadata
import io.rsocket.kotlin.metadata.security.BearerAuthMetadata
import io.rsocket.kotlin.payload.Payload
import io.rsocket.kotlin.payload.PayloadMimeType
import io.rsocket.kotlin.payload.buildPayload
import io.rsocket.kotlin.payload.data
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class SocketManager(private val engine: HttpClientEngine, private val tokenRepo: TokenRepo) {
    @OptIn(ExperimentalMetadataApi::class)
    suspend fun streamConversations(): Flow<String> {
        val rSocket: RSocket = getDefaultSocket(engine, tokenRepo).rSocket(BASE_SOCKET_URL)
        // request stream
        val stream: Flow<Payload> = rSocket.requestStream(
            buildPayload {
                data("""{ "data": "hello world" }""")
                metadata(
                    CompositeMetadata(
                        RoutingMetadata("v1/conversations"),
                        BearerAuthMetadata("Bearer ${tokenRepo.getToken()}")
                    )
                )
            }
        ).catch {
            if (it.message == "Unauthorized") {
                Logger.e(it) {
                    "Stream catch error $it"
                }
                tokenRepo.refreshToken()
            }
        }
        return stream.map {
            Logger.d("Stream success}")
            it.data.readText()
        }
    }

    companion object {
        const val BASE_SOCKET_URL = "ws://34.64.110.104/rsocket"
    }
}

private fun getDefaultSocket(engine: HttpClientEngine, tokenRepo: TokenRepo) = HttpClient(engine) {
    WebSockets {}
    install(RSocketSupport) {
        connector = RSocketConnector {
            connectionConfig {
                keepAlive = KeepAlive(30 * 1000, 120 * 1000)
                payloadMimeType = PayloadMimeType(
                    data = WellKnownMimeType.ApplicationJson,
                    metadata = WellKnownMimeType.MessageRSocketCompositeMetadata
                )
                setupPayload {
                    buildPayload {
                        data("hello")
                    }
                }
            }
            reconnectable { error, attempt ->
                Logger.e(error) {
                    "Stream error reconnect $error"
                }
                if (error.message == "Unauthorized") {
                    tokenRepo.refreshToken()
                }
                attempt <= 3
            }
        }
    }
}