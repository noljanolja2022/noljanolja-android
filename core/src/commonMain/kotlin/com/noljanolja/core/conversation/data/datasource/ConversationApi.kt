package com.noljanolja.core.conversation.data.datasource

import com.noljanolja.core.base.ResponseWithoutData
import com.noljanolja.core.conversation.data.model.request.*
import com.noljanolja.core.conversation.data.model.response.*
import com.noljanolja.core.conversation.data.model.response.GetConversationMessagesResponse
import com.noljanolja.core.conversation.domain.model.Conversation
import com.noljanolja.core.conversation.domain.model.ConversationType
import com.noljanolja.core.conversation.domain.model.MessageType
import com.noljanolja.core.utils.Const.BASE_URL
import com.noljanolja.core.utils.default
import com.noljanolja.socket.SocketManager
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class ConversationApi(
    private val client: HttpClient,
    private val socketManager: SocketManager,
) {

    suspend fun getConversations(): GetConversationsResponse {
        return client.get("$BASE_URL/api/v1/conversations").body()
    }

    suspend fun getConversation(
        request: GetConversationRequest,
    ): GetConversationResponse {
        return client.get("$BASE_URL/api/v1/conversations/${request.conversationId}").body()
    }

    suspend fun sendConversationMessage(
        request: SendConversationMessageRequest,
    ): SendConversationMessageResponse {
        return client.post("$BASE_URL/api/v1/conversations/${request.conversationId}/messages") {
            header(HttpHeaders.Accept, ContentType.MultiPart.FormData)
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("message", request.message.message)
                        append("type", request.message.type.name)
                        append("localId", request.message.localId)
                        request.replyToMessageId?.let {
                            append("replyToMessageId", it)
                        }
                        request.shareMessageId?.let {
                            append("shareMessageId", it)
                        }
                        request.shareVideoId?.let {
                            append("shareVideoId", it)
                        }

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

    suspend fun sendConversationsMessage(
        request: SendConversationsMessageRequest,
    ): ResponseWithoutData {
        return client.post("$BASE_URL/api/v1/conversations/messages") {
            header(HttpHeaders.Accept, ContentType.MultiPart.FormData)
            setBody(
                MultiPartFormDataContent(
                    formData {
                        request.conversationIds.forEach { id ->
                            append(
                                "conversationIds",
                                id,
                            )
                        }
                        append("message", request.message.message)
                        append("type", request.message.type.name)
                        append("localId", request.message.localId)
                        request.replyToMessageId?.let {
                            append("replyToMessageId", it)
                        }
                        request.shareMessageId?.let {
                            append("shareMessageId", it)
                        }
                        request.shareVideoId?.let {
                            append("shareVideoId", it)
                        }

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
        return client.submitFormWithBinaryData(
            "$BASE_URL/api/v1/conversations",
            formData {
                append("title", request.title)
                val type = request.type
                    ?: if (request.participantIds.size > 1) ConversationType.GROUP else ConversationType.SINGLE
                append("type", type.name)
                request.participantIds.forEach {
                    append("participantIds", it)
                }
            }
        ) {
            header(HttpHeaders.Accept, ContentType.MultiPart.FormData)
        }.body()
    }

    suspend fun streamConversations(
        token: String? = null,
        onError: suspend (Throwable, String?) -> Unit,
    ): Flow<Conversation> {
        return socketManager.streamConversations(token, onError).map {
            Json.default().decodeFromString(it)
        }
    }

    suspend fun trackVideoProgress(
        token: String? = null,
        data: String,
        onError: suspend (Throwable, String, String?) -> Unit,
    ) {
        socketManager.trackVideoProgress(token = token, data = data, onError = onError)
    }

    suspend fun getConversationMessages(
        request: GetConversationMessagesRequest,
    ): GetConversationMessagesResponse {
        return client.get("$BASE_URL/api/v1/conversations/${request.conversationId}/messages") {
            parameter("beforeMessageId", request.messageBefore)
            parameter("afterMessageId", request.messageAfter)
        }.body()
    }

    suspend fun updateMessageStatus(
        request: UpdateMessageStatusRequest,
    ) {
        return client.post(
            "$BASE_URL/api/v1/conversations/${request.conversationId}/messages/${request.messageId}/seen"
        ).body()
    }

    suspend fun addConversationParticipants(
        conversationId: Long,
        request: UpdateParticipantsRequest,
    ): ResponseWithoutData {
        return client.put("$BASE_URL/api/v1/conversations/$conversationId/participants") {
            setBody(request)
        }.body()
    }

    suspend fun removeConversationParticipants(
        conversationId: Long,
        request: UpdateParticipantsRequest,
    ): ResponseWithoutData {
        val participantIds = request.participantIds.joinToString(",")
        return client.delete("$BASE_URL/api/v1/conversations/$conversationId/participants?participantIds=$participantIds") {
        }.body()
    }

    suspend fun makeConversationAdmin(
        conversationId: Long,
        request: AssignAdminRequest,
    ): ResponseWithoutData {
        return client.put("$BASE_URL/api/v1/conversations/$conversationId/admin") {
            setBody(request)
        }.body()
    }

    suspend fun updateConversation(
        conversationId: Long,
        request: UpdateConversationRequest,
    ): GetConversationResponse {
        return client.put("$BASE_URL/api/v1/conversations/$conversationId") {
            header(HttpHeaders.Accept, ContentType.MultiPart.FormData)
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("title", request.title)
                    }
                )
            )
        }.body()
    }

    suspend fun getReactIcons(): GetReactIconsResponse {
        return client.get("$BASE_URL/api/v1/conversations/react-icons").body()
    }

    suspend fun reactMessage(request: ReactRequest): ResponseWithoutData {
        return client.put("$BASE_URL/api/v1/conversations/${request.conversationId}/messages/${request.messageId}/reactions/${request.reactId}")
            .body()
    }

    suspend fun deleteMessage(
        conversationId: Long,
        messageId: Long,
        removeForSelfOnly: Boolean,
    ): ResponseWithoutData {
        return client.delete("$BASE_URL/api/v1/conversations/$conversationId/messages/$messageId") {
            parameter("removeForSelfOnly", removeForSelfOnly)
        }
            .body()
    }

    suspend fun getConversationAttachments(
        conversationId: Long,
        attachmentTypes: List<String>,
        page: Int = 0,
    ): GetConversationMediasResponse {
        return client.get("$BASE_URL/api/v1/conversations/$conversationId/attachments") {
            parameter("attachmentTypes", attachmentTypes.joinToString(","))
            parameter("page", page)
        }
            .body()
    }
}