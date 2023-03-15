package com.noljanolja.core.conversation.data.repository

import co.touchlab.kermit.Logger
import com.noljanolja.core.conversation.data.datasource.ConversationApi
import com.noljanolja.core.conversation.data.datasource.LocalConversationDataSource
import com.noljanolja.core.conversation.data.model.request.CreateConversationRequest
import com.noljanolja.core.conversation.data.model.request.GetConversationMessagesRequest
import com.noljanolja.core.conversation.data.model.request.GetConversationRequest
import com.noljanolja.core.conversation.data.model.request.SendConversationMessageRequest
import com.noljanolja.core.conversation.domain.model.Conversation
import com.noljanolja.core.conversation.domain.model.ConversationType
import com.noljanolja.core.conversation.domain.model.Message
import com.noljanolja.core.conversation.domain.model.MessageStatus
import com.noljanolja.core.conversation.domain.repository.ConversationRepository
import com.noljanolja.core.user.data.datasource.LocalUserDataSource
import com.noljanolja.core.user.domain.repository.UserRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.random.Random

class ConversationRepositoryImpl(
    private val conversationApi: ConversationApi,
    private val userRepository: UserRepository,
    private val localConversationDataSource: LocalConversationDataSource,
    private val localUserDataSource: LocalUserDataSource,
) : ConversationRepository {

    private val scope = CoroutineScope(Dispatchers.Default)
    private var job: Job? = null
    override suspend fun findConversationWithUser(userId: String): Conversation? {
        return localConversationDataSource.findSingleConversationWithUser(userId)
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
            localConversationDataSource.upsertConversationMessages(
                conversationId,
                listOf(sentMessage)
            )
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
                localConversationDataSource.upsertConversationMessages(conversationId, messages)
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

    private suspend fun getLocalConversation(conversationId: Long): Flow<Conversation> {
        return localConversationDataSource.findById(conversationId)
            .combine(localConversationDataSource.findConversationMessages(conversationId)) { conversation, messages ->
                localUserDataSource.let {
                    conversation.copy(
                        creator = it.findById(conversation.creator.id) ?: conversation.creator,
                        participants = it.findConversationParticipants(conversation.id),
                        messages = messages.map { message ->
                            message.copy(sender = it.findById(message.sender.id) ?: message.sender)
                        }
                    )
                }
            }
    }

    private suspend fun updateLocalConversation(
        conversation: Conversation,
        saveCreator: Boolean = true,
        saveParticipants: Boolean = true,
        saveMessage: Boolean = true,
        saveMyMessage: Boolean = true,
        saveSender: Boolean = true,
    ) {
        val me = localUserDataSource.findMe() ?: return
        localConversationDataSource.upsert(conversation)
        if (saveCreator) localUserDataSource.upsert(conversation.creator)
        if (saveParticipants) {
            localUserDataSource.upsertConversationParticipants(
                conversation.id,
                conversation.participants
            )
        }
        if (saveMessage) {
            localConversationDataSource.upsertConversationMessages(
                conversation.id,
                conversation.messages
                    .filter { if (saveMyMessage) true else it.sender.id != me.id }
                    .map { it.copy(status = MessageStatus.SENT) }
            )
        }
        if (saveSender) {
            conversation.messages.map { it.sender }.distinctBy { it.id }
                .forEach { localUserDataSource.upsert(it) }
        }
    }

    private suspend fun getLocalConversations(): Flow<List<Conversation>> {
        return localConversationDataSource.findAll().map { localConversations ->
            localConversations.mapNotNull { localConversation ->
                val messages = localConversationDataSource.findConversationMessages(
                    localConversation.id,
                    limit = 1
                )
                    .firstOrNull() ?: listOf()
                if (messages.isNotEmpty()) {
                    Conversation(
                        id = localConversation.id,
                        title = localConversation.title,
                        type = localConversation.type,
                        creator = localUserDataSource.findById(localConversation.creator.id)
                            ?: localConversation.creator,
                        participants = localUserDataSource.findConversationParticipants(
                            localConversation.id,
                            limit = 4
                        ),
                        messages = messages.map {
                            it.copy(
                                sender = localUserDataSource.findById(it.sender.id) ?: it.sender
                            )
                        },
                        createdAt = localConversation.createdAt,
                        updatedAt = localConversation.updatedAt,
                    )
                } else {
                    null
                }
            }
        }
    }

    fun onDestroy() {
        scope.coroutineContext.cancel()
    }
}