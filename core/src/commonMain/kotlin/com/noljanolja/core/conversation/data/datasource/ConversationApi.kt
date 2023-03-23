package com.noljanolja.core.conversation.data.datasource

import com.noljanolja.core.conversation.data.model.request.*
import com.noljanolja.core.conversation.data.model.response.*
import com.noljanolja.core.conversation.data.model.response.GetConversationMessagesResponse
import com.noljanolja.core.conversation.domain.model.Conversation
import com.noljanolja.core.conversation.domain.model.MessageType
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
            header(HttpHeaders.Accept, ContentType.MultiPart.FormData)
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("message", request.message.message)
                        append("type", request.message.type.name)
                        when (request.message.type) {
                            MessageType.PHOTO,
                            MessageType.DOCUMENT,
                            -> {
                                request.message.attachments.forEach { attachment ->
                                    append(
                                        "attachments",
                                        attachment.contents,
                                        Headers.build {
                                            append(HttpHeaders.ContentType, attachment.type)
                                            append(HttpHeaders.ContentLength, attachment.size)
                                            append(
                                                HttpHeaders.ContentDisposition,
                                                "filename=${attachment.originalName}"
                                            )
                                        }
                                    )
                                }
                            }
                            else -> {}
                        }
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
        return socketManager.streamConversations().map {
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

    suspend fun updateMessageStatus(
        request: UpdateMessageStatusRequest,
    ) {
        return client.post(
            "$BASE_URL/conversations/${request.conversationId}/messages/${request.messageId}/seen"
        ).body()
    }
}