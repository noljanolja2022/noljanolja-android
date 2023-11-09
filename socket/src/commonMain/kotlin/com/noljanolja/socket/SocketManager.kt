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
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class SocketManager(
    private val engine: HttpClientEngine,
    private val tokenRepo: TokenRepo,
    private val userAgent: SocketUserAgent,
) {
    private var videoRSocket: RSocket? = null

    suspend fun trackVideoProgress(
        token: String? = null,
        data: String,
        onError: suspend (error: Throwable, failData: String, newToken: String?) -> Unit,
    ) {
        val rSocket =
            videoRSocket ?: getDefaultSocket(
                engine,
                tokenRepo,
                userAgent
            ).rSocket("$BASE_URL/rsocket")
                .also {
                    videoRSocket = it
                }
        val streamToken = token ?: tokenRepo.getToken().takeIf { !it.isNullOrBlank() }
        try {
            rSocket.fireAndForget(
                buildPayload {
                    data(data)
                    metadata(
                        CompositeMetadata(
                            RoutingMetadata("v1/videos"),
                            BearerAuthMetadata("Bearer $streamToken")
                        )
                    )
                }
            )
        } catch (error: Throwable) {
            Logger.e(error) {
                "FireAndForget error catch $error"
            }
            val newToken = if (error.message?.contains("Unauthorized") == true) {
                tokenRepo.refreshToken()
            } else {
                null
            }
            cancelTrackVideo()
            onError.invoke(error, data, newToken)
            error.printStackTrace()
        }
    }

    fun cancelTrackVideo() {
        videoRSocket?.cancel()
        videoRSocket = null
    }

    @OptIn(ExperimentalMetadataApi::class)
    suspend fun streamConversations(
        token: String? = null,
        onError: suspend (error: Throwable, newToken: String?) -> Unit,
    ): Flow<String> {
        try {
            val rSocket: RSocket =
                getDefaultSocket(engine, tokenRepo, userAgent).rSocket("$BASE_URL/rsocket")
            // request stream
            val streamToken = token ?: tokenRepo.getToken().takeIf { !it.isNullOrBlank() }
            return streamToken?.let { streamToken ->
                val stream: Flow<Payload> = rSocket.requestStream(
                    buildPayload {
                        data("""{ "data": "hello world" }""")
                        metadata(
                            CompositeMetadata(
                                RoutingMetadata("v1/conversations"),
                                BearerAuthMetadata("Bearer $streamToken")
                            )
                        )
                    }
                ).catch { error ->
                    Logger.e(error) {
                        "Stream error catch $error ${error.message}"
                    }
                    val newToken = if (error.message?.contains("Unauthorized") == true) {
                        tokenRepo.refreshToken()
                    } else {
                        null
                    }
                    onError.invoke(error, newToken)
                }
                stream.map {
                    it.data.readText()
                }
            } ?: throw Throwable()
        } catch (e: Throwable) {
            return flow { }
        }
    }
}

private fun getDefaultSocket(
    engine: HttpClientEngine,
    tokenRepo: TokenRepo,
    userAgent: SocketUserAgent,
) =
    HttpClient(engine) {
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
                    attempt <= 3
                }
            }
        }
        install(DefaultRequest) {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            header(HttpHeaders.UserAgent, userAgent.userAgent)
        }
    }

data class SocketUserAgent(val userAgent: String)