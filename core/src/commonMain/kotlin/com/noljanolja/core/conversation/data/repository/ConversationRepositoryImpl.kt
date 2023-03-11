package com.noljanolja.core.conversation.data.repository

import co.touchlab.kermit.Logger
import com.noljanolja.core.conversation.data.datasource.ConversationApi
import com.noljanolja.core.conversation.data.model.request.CreateConversationRequest
import com.noljanolja.core.conversation.data.model.request.GetConversationMessagesRequest
import com.noljanolja.core.conversation.data.model.request.GetConversationRequest
import com.noljanolja.core.conversation.data.model.request.SendConversationMessageRequest
import com.noljanolja.core.conversation.domain.model.Conversation
import com.noljanolja.core.conversation.domain.model.ConversationType
import com.noljanolja.core.conversation.domain.model.Message
import com.noljanolja.core.conversation.domain.model.MessageStatus
import com.noljanolja.core.conversation.domain.repository.ConversationRepository
import com.noljanolja.core.user.domain.repository.UserRepository
import com.noljanolja.core.utils.Database.findSingleConversationWithUser
import com.noljanolja.core.utils.Database.getLocalConversation
import com.noljanolja.core.utils.Database.getLocalConversations
import com.noljanolja.core.utils.Database.updateLocalConversation
import com.noljanolja.core.utils.Database.upsertConversationMessages
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

class ConversationRepositoryImpl(
    private val conversationApi: ConversationApi,
    private val userRepository: UserRepository,
) : ConversationRepository {

    private val scope = CoroutineScope(Dispatchers.Default)
    private var job: Job? = null
    override suspend fun findConversationWithUser(userId: String): Conversation? {
        return findSingleConversationWithUser(userId)
    }

    override suspend fun getConversation(conversationId: Long): Flow<Conversation> = flow {
        try {
            conversationApi.getConversation(GetConversationRequest(conversationId)).data?.let {
                updateLocalConversation(it)
            }
            getLocalConversation(conversationId).collect {
                emit(it)
            }
        } catch (e: Throwable) {
            // Logger
        }
    }

    override suspend fun getConversations(): Flow<List<Conversation>> = flow {
        try {
            scope.launch {
                conversationApi.getConversations().data?.forEach {
                    updateLocalConversation(it)
                }
            }
            streamConversations()
            getLocalConversations().collect {
                emit(it.sortedByDescending { it.messages.maxByOrNull { it.createdAt }?.createdAt })
            }
        } catch (e: Throwable) {
            emit(emptyList())
        }
    }

    override suspend fun sendConversationMessage(
        conversationId: Long,
        userId: String,
        message: Message,
    ): Long {
        val sentConversationId =
            if (conversationId == 0L) createConversation(userId) else conversationId

        if (sentConversationId != 0L) {
            val sendingMessage = message.copy(
                sender = userRepository.getCurrentUser().getOrNull()!!,
                status = MessageStatus.SENDING,
            )
            val response = conversationApi.sendConversationMessage(
                SendConversationMessageRequest(
                    conversationId = sentConversationId,
                    message = sendingMessage,
                )
            )
            val sentMessage = if (response.isSuccessful() && response.data != null) {
                response.data.copy(
                    localId = sendingMessage.localId,
                    status = MessageStatus.SENT,
                )
            } else {
                sendingMessage.copy(
                    id = Random.nextLong(1_000_000_000, 999_999_999_999),
                    status = MessageStatus.FAILED,
                )
            }
            upsertConversationMessages(conversationId, listOf(sentMessage))
            return sentConversationId
        }
        return 0L
    }

    private suspend fun createConversation(userId: String): Long {
        return conversationApi.createConversation(
            CreateConversationRequest(
                title = "",
                type = ConversationType.SINGLE,
                participantIds = listOf(userId)
            )
        ).data?.id ?: 0L
    }

    private fun streamConversations() {
        job?.cancel()
        job = scope.launch {
            conversationApi.streamConversations().collect {
                updateLocalConversation(it)
            }
        }
    }

    override suspend fun getConversationMessages(
        conversationId: Long,
        messageBefore: Long?,
        messageAfter: Long?,
    ): List<Message> {
        try {
            val response = conversationApi.getConversationMessages(
                GetConversationMessagesRequest(conversationId, messageBefore, messageAfter)
            )
            return if (response.isSuccessful()) {
                val messages =
                    (response.data ?: listOf()).map { it.copy(status = MessageStatus.SENT) }
                upsertConversationMessages(conversationId, messages)
                messages
            } else {
                listOf()
            }
        } catch (error: Throwable) {
            Logger.e(error) {
                "getConversationMessages"
            }
            return listOf()
        }
    }

    fun onDestroy() {
        scope.coroutineContext.cancel()
    }
}