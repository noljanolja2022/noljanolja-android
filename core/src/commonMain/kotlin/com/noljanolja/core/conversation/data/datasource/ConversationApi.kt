package com.noljanolja.core.conversation.data.datasource

import com.noljanolja.core.auth.domain.repository.AuthRepository
import com.noljanolja.core.conversation.data.model.request.CreateConversationRequest
import com.noljanolja.core.conversation.data.model.request.GetConversationMessagesRequest
import com.noljanolja.core.conversation.data.model.request.GetConversationRequest
import com.noljanolja.core.conversation.data.model.request.SendConversationMessageRequest
import com.noljanolja.core.conversation.data.model.response.*
import com.noljanolja.core.conversation.data.model.response.GetConversationMessagesResponse
import com.noljanolja.core.conversation.domain.model.Conversation
import com.noljanolja.core.utils.Const.BASE_URL
import com.noljanolja.core.utils.default
import com.noljanolja.socket.SocketManager
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.util.*
import io.rsocket.kotlin.payload.data
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class ConversationApi(
    private val client: HttpClient,
    private val authRepository: AuthRepository,
    private val socketManager: SocketManager,
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

    suspend fun streamConversations(): Flow<Conversation> {
        val token = authRepository.getAuthToken()
        return socketManager.streamConversations(
            token.orEmpty()
        ).map {
            Json.default().decodeFromString(it)
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