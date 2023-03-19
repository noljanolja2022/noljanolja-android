package com.noljanolja.socket.di

import co.touchlab.kermit.Logger
import com.noljanolja.socket.SocketManager
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.rsocket.kotlin.core.RSocketConnector
import io.rsocket.kotlin.core.WellKnownMimeType
import io.rsocket.kotlin.keepalive.KeepAlive
import io.rsocket.kotlin.ktor.client.RSocketSupport
import io.rsocket.kotlin.payload.PayloadMimeType
import io.rsocket.kotlin.payload.buildPayload
import io.rsocket.kotlin.payload.data
import org.koin.core.qualifier.named
import org.koin.dsl.module

val socketModule = module {
    single(named("rSocket")) {
        HttpClient(get()) {
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
                        Logger.e(error, { "Stream error 1 $error" })
                        attempt <= 3
                    }
                }
            }
        }
    }
    single {
        SocketManager(get(named("rSocket")), get())
    }
}