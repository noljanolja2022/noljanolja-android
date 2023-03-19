package com.noljanolja.socket

import co.touchlab.kermit.Logger
import io.ktor.client.*
import io.rsocket.kotlin.ExperimentalMetadataApi
import io.rsocket.kotlin.RSocket
import io.rsocket.kotlin.ktor.client.rSocket
import io.rsocket.kotlin.metadata.CompositeMetadata
import io.rsocket.kotlin.metadata.RoutingMetadata
import io.rsocket.kotlin.metadata.metadata
import io.rsocket.kotlin.metadata.security.BearerAuthMetadata
import io.rsocket.kotlin.payload.Payload
import io.rsocket.kotlin.payload.buildPayload
import io.rsocket.kotlin.payload.data
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class SocketManager(private val socketClient: HttpClient, private val tokenRepo: TokenRepo) {
    @OptIn(ExperimentalMetadataApi::class)
    suspend fun streamConversations(): Flow<String> {
        val rSocket: RSocket = socketClient.rSocket(BASE_SOCKET_URL)
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
                    "Stream error 0 $it"
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