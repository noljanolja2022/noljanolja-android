package com.noljanolja.core.conversation.data.repository

import co.touchlab.kermit.Logger
import com.noljanolja.core.conversation.data.datasource.ConversationApi
import com.noljanolja.core.conversation.data.datasource.LocalConversationDataSource
import com.noljanolja.core.conversation.data.model.request.*
import com.noljanolja.core.conversation.domain.model.*
import com.noljanolja.core.conversation.domain.repository.ConversationRepository
import com.noljanolja.core.user.data.datasource.LocalUserDataSource
import com.noljanolja.core.user.domain.repository.UserRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

internal class ConversationRepositoryImpl(
    private val conversationApi: ConversationApi,
    private val userRepository: UserRepository,
    private val localConversationDataSource: LocalConversationDataSource,
    private val localUserDataSource: LocalUserDataSource,
) : ConversationRepository {

    private val scope = CoroutineScope(Dispatchers.Default)
    private var job: Job? = null

    override suspend fun findConversationWithUsers(userIds: List<String>): Conversation? {
        return if (userIds.size == 1) {
            localConversationDataSource.findSingleConversationWithUser(userIds.first())
        } else {
            null
        }
    }

    override suspend fun getConversation(conversationId: Long): Flow<Conversation> = flow {
        try {
            scope.launch {
                conversationApi.getConversation(GetConversationRequest(conversationId)).data?.let {
                    updateLocalConversation(it)
                }
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
            getLocalConversations().collect {
                emit(it.sortedByDescending { it.messages.maxByOrNull { it.createdAt }?.createdAt })
            }
        } catch (e: Throwable) {
            emit(emptyList())
        }
    }

    override suspend fun sendConversationMessage(
        title: String,
        conversationId: Long,
        userIds: List<String>,
        message: Message,
    ): Long {
        val sentConversationId =
            if (conversationId == 0L) createConversation(title, userIds) else conversationId

        if (sentConversationId != 0L) {
            val sendingMessage = message.copy(
                sender = userRepository.getCurrentUser().getOrNull()!!,
                status = MessageStatus.SENDING,
            )
            localConversationDataSource.upsertConversationMessages(
                sentConversationId,
                listOf(sendingMessage)
            )
            val response = conversationApi.sendConversationMessage(
                SendConversationMessageRequest(
                    conversationId = sentConversationId,
                    message = sendingMessage,
                )
            )
            Logger.e("Receive: send response ${response.data}")

            if (!response.isSuccessful() || response.data == null) {
                localConversationDataSource.upsertConversationMessages(
                    sentConversationId,
                    listOf(
                        sendingMessage.copy(
                            status = MessageStatus.FAILED,
                        )
                    )
                )
            } else {
                localConversationDataSource.upsertConversationMessages(
                    sentConversationId,
                    listOf(
                        response.data.copy(
                            status = MessageStatus.SENT,
                            _localId = sendingMessage.localId
                        )
                    )
                )
            }

            return sentConversationId
        }
        return 0L
    }

    private suspend fun createConversation(title: String, userIds: List<String>): Long {
        return conversationApi.createConversation(
            CreateConversationRequest(
                title = title,
                participantIds = userIds
            )
        ).data?.id ?: 0L
    }

    override suspend fun streamConversations() {
        Logger.e("Stream start")
        job?.cancel()
        job = scope.launch {
            conversationApi.streamConversations()
                .catch { error ->
                    Logger.e(error) {
                        "streamConversations"
                    }
                }
                .collect {
                    Logger.e("Receive: Stream ${it.messages}")
                    updateLocalConversation(
                        it,
                    )
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
                    val participants = it.findConversationParticipants(conversation.id)
                    conversation.copy(
                        creator = it.findById(conversation.creator.id)
                            ?: conversation.creator,
                        participants = participants,
                        messages = messages.mapNotNull { message ->
                            if (message.message.isEmpty() && message.attachments.isEmpty()) {
                                null
                            } else {
                                val sender = it.findById(message.sender.id) ?: message.sender
                                val myId = it.findMe()?.id ?: 0L
                                message.copy(sender = sender)
                                    .apply {
                                        if (seenBy.any { it.isNotBlank() }) {
                                            seenUsers =
                                                seenBy.mapNotNull { id -> participants.find { it.id == id } }
                                        }
                                        isSeenByMe = sender.isMe || seenBy.contains(myId)
                                    }
                            }
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
                conversation.messages.filter { if (saveMyMessage) true else it.sender.id != me.id }
                    .map { it.copy(status = MessageStatus.SENT) }
            )
        }
        if (saveSender) {
            conversation.messages.map { it.sender }.distinctBy { it.id }
                .forEach { localUserDataSource.upsert(it) }
        }
        localConversationDataSource.upsert(conversation)
    }

    private suspend fun getLocalConversations(): Flow<List<Conversation>> {
        return localConversationDataSource.findAll().map { localConversations ->
            localConversations.mapNotNull { localConversation ->
                // wait update message db
                val messages = localConversationDataSource.findConversationMessages(
                    localConversation.id,
                    limit = 1
                ).firstOrNull() ?: listOf()
                if (messages.isNotEmpty()) {
                    val myId = localUserDataSource.findMe()?.id ?: 0
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
                            ).apply {
                                isSeenByMe = sender.isMe || seenBy.contains(myId)
                            }
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

    override suspend fun upsertConversationMessages(conversationId: Long, messages: List<Message>) {
        localConversationDataSource.upsertConversationMessages(
            conversationId = conversationId,
            messages = messages
        )
    }

    override suspend fun updateMessageStatus(conversationId: Long, messageId: Long) {
        conversationApi.updateMessageStatus(
            UpdateMessageStatusRequest(conversationId = conversationId, messageId = messageId)
        )
    }

    override fun onDestroy() {
        job?.cancel()
        scope.coroutineContext.cancel()
    }
}