package com.noljanolja.core.conversation.data.datasource

import com.noljanolja.core.conversation.data.model.request.CreateConversationRequest
import com.noljanolja.core.conversation.data.model.request.GetConversationMessagesRequest
import com.noljanolja.core.conversation.data.model.request.GetConversationRequest
import com.noljanolja.core.conversation.data.model.request.SendConversationMessageRequest
import com.noljanolja.core.conversation.data.model.response.*
import com.noljanolja.core.conversation.data.model.response.GetConversationMessagesResponse
import com.noljanolja.core.conversation.domain.model.Conversation
import com.noljanolja.core.utils.Const.BASE_SOCKET_URL
import com.noljanolja.core.utils.Const.BASE_URL
import com.noljanolja.core.utils.Database
import com.noljanolja.core.utils.default
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.util.*
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
import kotlinx.coroutines.flow.map
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class ConversationApi(
    private val client: HttpClient,
    private val socketClient: HttpClient,
) {

    suspend fun getConversations(): GetConversationsResponse {
        return client.get("$BASE_URL/conversations").body()
    }

    suspend fun getConversation(
        request: GetConversationRequest,
    ): GetConversationResponse {
        return client.get("$BASE_URL/conversations/${request.conversationId}").body()
    }

    suspend fun sendConversationMessage(
        request: SendConversationMessageRequest,
    ): SendConversationMessageResponse {
        return client.post("$BASE_URL/conversations/${request.conversationId}/messages") {
            if (!request.message.message.contains("fail")) {
                header(HttpHeaders.Accept, ContentType.MultiPart.FormData)
            }
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("message", request.message.message)
                        append("type", request.message.type.name)
                    },
                )
            )
        }.body()
    }

    suspend fun createConversation(
        request: CreateConversationRequest,
    ): CreateConversationResponse {
        return client.post("$BASE_URL/conversations") {
            setBody(request)
        }.body()
    }

    @OptIn(ExperimentalMetadataApi::class, ExperimentalSerializationApi::class)
    suspend fun streamConversations(): Flow<Conversation> {
        val token = Database.getToken()
        val rSocket: RSocket = socketClient.rSocket(BASE_SOCKET_URL)
        // request stream
        val stream: Flow<Payload> = rSocket.requestStream(
            buildPayload {
                data("""{ "data": "hello world" }""")
                metadata(
                    CompositeMetadata(
                        RoutingMetadata("v1/conversations"),
                        BearerAuthMetadata("Bearer $token")
                    )
                )
            }
        )
        return stream.map {
            Json.default().decodeFromPayload(it)
        }
    }

    suspend fun getConversationMessages(
        request: GetConversationMessagesRequest,
    ): GetConversationMessagesResponse {
        return client.get("$BASE_URL/conversations/${request.conversationId}/messages") {
            parameter("beforeMessageId", request.messageBefore)
            parameter("afterMessageId", request.messageAfter)
        }.body()
    }
}

@ExperimentalSerializationApi
internal inline fun <reified T> Json.decodeFromPayload(
    payload: Payload,
): T = decodeFromString(payload.data.readText())