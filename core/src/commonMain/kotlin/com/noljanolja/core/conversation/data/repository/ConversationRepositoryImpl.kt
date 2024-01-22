package com.noljanolja.core.conversation.data.repository

import co.touchlab.kermit.*
import com.noljanolja.core.conversation.data.datasource.*
import com.noljanolja.core.conversation.data.model.request.*
import com.noljanolja.core.conversation.domain.model.*
import com.noljanolja.core.conversation.domain.repository.*
import com.noljanolja.core.shop.data.datasource.*
import com.noljanolja.core.shop.domain.model.*
import com.noljanolja.core.user.data.datasource.*
import com.noljanolja.core.user.domain.model.*
import com.noljanolja.core.utils.*
import com.noljanolja.core.video.data.model.request.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*

internal class ConversationRepositoryImpl(
    private val conversationApi: ConversationApi,
    private val searchLocalDatasource: SearchLocalDatasource,
    private val localConversationDataSource: LocalConversationDataSource,
    private val localUserDataSource: LocalUserDataSource,
    private val reactDataSource: LocalReactDataSource,
) : ConversationRepository {

    private val scope = CoroutineScope(Dispatchers.Default)
    private var streamJob: Job? = null
    private var trackJob: Job? = null
    private var reactJob: Job? = null

    private val _removedConversationEvent = MutableSharedFlow<Conversation>()
    override val removedConversationEvent: SharedFlow<Conversation>
        get() = _removedConversationEvent.asSharedFlow()

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
                try {
                    conversationApi.getConversation(GetConversationRequest(conversationId)).data?.let {
                        updateLocalConversation(it)
                    }
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
            getLocalConversation(conversationId).collect {
                emit(it)
            }
        } catch (e: Throwable) {
            // Logger
        }
    }

    override suspend fun fetchConversations(): Flow<List<Conversation>> = flow {
        try {
            forceRefreshConversations()
            getLocalConversations().collect {
                emit(
                    it.sortedByDescending {
                        it.messages.maxByOrNull { it.createdAt }?.createdAt ?: it.createdAt
                    }
                )
            }
        } catch (e: Throwable) {
            emit(emptyList())
        }
    }

    override suspend fun forceRefreshConversations() {
        scope.launch {
            try {
                conversationApi.getConversations().data?.let { conversations ->
                    localConversationDataSource.deleteConversationNotInIds(conversations.map { it.id })
                    conversations.forEach {
                        updateLocalConversation(it)
                    }
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    override suspend fun sendConversationMessage(
        title: String,
        conversationId: Long,
        userIds: List<String>,
        message: Message,
        replyToMessageId: Long?,
        shareMessageId: Long?,
        shareVideoId: String?,
    ): Long {
        val sentConversationId =
            if (conversationId == 0L) createConversation(title, userIds) else conversationId

        if (sentConversationId != 0L) {
            val sendingMessage = message.copy(
                sender = localUserDataSource.findMe()!!,
                status = MessageStatus.SENDING,
            )
            localConversationDataSource.upsertConversationMessages(
                sentConversationId,
                listOf(sendingMessage)
            )
            try {
                val response =
                    conversationApi.sendConversationMessage(
                        SendConversationMessageRequest(
                            conversationId = sentConversationId,
                            message = sendingMessage,
                            replyToMessageId = replyToMessageId,
                            shareMessageId = shareMessageId,
                            shareVideoId = shareVideoId
                        )
                    )

                Logger.e("Receive: send response ${response.data}")

                if (!response.isSuccessful() || response.data == null) {
                    throw Throwable(response.message)
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
            } catch (e: Throwable) {
                localConversationDataSource.upsertConversationMessages(
                    sentConversationId,
                    listOf(
                        sendingMessage.copy(
                            status = MessageStatus.FAILED,
                        )
                    )
                )
                e.printStackTrace()
            }

            return sentConversationId
        }
        return 0L
    }

    override suspend fun sendConversationsMessage(
        conversationIds: List<Long>,
        userIds: List<String>,
        message: Message,
        replyToMessageId: Long?,
        shareMessageId: Long?,
        shareVideoId: String?,
        title: String?,
    ): Result<Boolean> {
        val sentConversationId =
            if ((conversationIds.firstOrNull() ?: 0L) == 0L) {
                createConversation(
                    title.orEmpty(),
                    userIds
                )
            } else {
                conversationIds.firstOrNull()
            }
        val sendingMessage = message.copy(
            sender = localUserDataSource.findMe()!!,
            status = MessageStatus.SENDING,
        )
        try {
            val response =
                conversationApi.sendConversationsMessage(
                    SendConversationsMessageRequest(
                        conversationIds = listOfNotNull(sentConversationId),
                        message = sendingMessage,
                        replyToMessageId = replyToMessageId,
                        shareMessageId = shareMessageId,
                        shareVideoId = shareVideoId,
                    )
                )

            Logger.e("Receive: send response ${response.data}")

            if (!response.isSuccessful()) {
                throw Throwable(response.message)
            }
            return Result.success(true)
        } catch (e: Throwable) {
            return Result.failure(e)
        }
    }

    override suspend fun createConversation(title: String, userIds: List<String>): Long {
        return try {
            conversationApi.createConversation(
                CreateConversationRequest(
                    title = title,
                    participantIds = userIds
                )
            ).also {
                it.data?.let { updateLocalConversation(it) }
            }.data?.id ?: 0L
        } catch (e: Throwable) {
            e.printStackTrace()
            0L
        }
    }

    override suspend fun streamConversations(
        token: String?,
    ) {
        streamJob?.cancel()
        streamJob = scope.launch {
            val me = localUserDataSource.findMe() ?: return@launch
            conversationApi.streamConversations(token, ::onStreamError)
                .collect { conversation ->
                    val message = conversation.messages.firstOrNull()
                    Logger.d("Stream success: ${conversation.id}")

                    when {
                        me.isRemoveFromConversation(conversation) -> {
                            localConversationDataSource.deleteById(conversation.id)
                            _removedConversationEvent.emit(conversation)
                        }

                        message?.isDeleted == true -> {
                            localConversationDataSource.deleteConversationMessageById(message.id)
                        }

                        else -> {
                            updateLocalConversation(
                                conversation,
                            )
                        }
                    }
                }
        }
    }

    override suspend fun trackVideoProgress(
        token: String?,
        videoId: String,
        event: VideoProgressEvent,
        durationMs: Long,
    ) {
        trackJob?.cancel()
        trackJob = scope.launch {
            val data = Json.default().encodeToString(
                VideoProgressRequest(
                    videoId = videoId,
                    event = event,
                    durationMs = durationMs
                )
            )
            conversationApi.trackVideoProgress(token = token, data = data, onError = ::onTrackError)
        }
    }

    override suspend fun leaveConversation(conversationId: Long): Result<Boolean> {
        return try {
            val user = localUserDataSource.findMe()!!
            val response = conversationApi.removeConversationParticipants(
                conversationId,
                UpdateParticipantsRequest(listOf(user.id))
            )
            if (response.isSuccessful()) {
                Result.success(true).also {
                    localConversationDataSource.deleteConversationMessages(conversationId)
                    localConversationDataSource.deleteAllConversationParticipants(conversationId)
                    localConversationDataSource.deleteById(conversationId)
                }
            } else {
                throw Error(response.message)
            }
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    override suspend fun addParticipants(
        conversationId: Long,
        userIds: List<String>,
    ): Result<Boolean> {
        return try {
            val response = conversationApi.addConversationParticipants(
                conversationId,
                UpdateParticipantsRequest(userIds)
            )
            return if (response.isSuccessful()) {
                Result.success(true)
            } else {
                throw Error(response.message)
            }
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    override suspend fun removeParticipants(
        conversationId: Long,
        userIds: List<String>,
    ): Result<Boolean> {
        return try {
            val response = conversationApi.removeConversationParticipants(
                conversationId,
                UpdateParticipantsRequest(userIds)
            )
            if (response.isSuccessful()) {
                Result.success(true).also {
                    localConversationDataSource.deleteConversationParticipants(
                        conversationId,
                        userIds
                    )
                }
            } else {
                throw Error(response.message)
            }
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    override suspend fun makeConversationAdmin(
        conversationId: Long,
        userId: String,
    ): Result<Boolean> {
        return try {
            val response = conversationApi.makeConversationAdmin(
                conversationId,
                AssignAdminRequest(userId)
            )
            return if (response.isSuccessful()) {
                Result.success(true)
            } else {
                throw Error(response.message)
            }
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    override suspend fun updateConversation(
        conversationId: Long,
        title: String,
    ): Result<Conversation> {
        return try {
            val response = conversationApi.updateConversation(
                conversationId,
                UpdateConversationRequest(title = title)
            )
            return response.data?.let {
                updateLocalConversation(it)
                Result.success(it)
            } ?: throw Error(response.message)
        } catch (e: Throwable) {
            Result.failure(e)
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

    private suspend fun onStreamError(error: Throwable, newToken: String?) {
        streamConversations(newToken)
    }

    private suspend fun onTrackError(error: Throwable, failData: String, newToken: String?) {
        trackJob?.cancel()
        trackJob = scope.launch {
            conversationApi.trackVideoProgress(
                token = newToken,
                data = failData,
                onError = ::onTrackError
            )
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
                        messages = messages.map { message ->
                            val sender = it.findById(message.sender.id) ?: message.sender
                            val myId = it.findMe()?.id ?: 0L
                            val textMessage =
                                if (message.type == MessageType.EVENT_JOINED) {
                                    message.message.convertIdsToNames(
                                        participants
                                    )
                                } else {
                                    message.message
                                }
                            message.copy(
                                sender = sender,
                                message = textMessage
                            ).apply {
                                if (seenBy.any { it.isNotBlank() }) {
                                    seenUsers =
                                        seenBy.mapNotNull { id -> participants.find { it.id == id } }
                                }
                                isSeenByMe = sender.isMe || seenBy.contains(myId)
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
            localUserDataSource.deleteByNotInUsers(
                conversation.id,
                userIds = conversation.participants.map { it.id }
            )
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

    override suspend fun getLocalConversations(): Flow<List<Conversation>> {
        return localConversationDataSource.findAll().map { localConversations ->
            localConversations.map { localConversation ->
                // wait update message db
                val messages = localConversationDataSource.findConversationMessages(
                    localConversation.id,
                    limit = 1
                ).firstOrNull() ?: listOf()

                val myId = localUserDataSource.findMe()?.id ?: 0
                Conversation(
                    id = localConversation.id,
                    title = localConversation.title,
                    type = localConversation.type,
                    creator = localUserDataSource.findById(localConversation.creator.id)
                        ?: localConversation.creator,
                    admin = localConversation.admin,
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
            }
        }.map {
            it.sortedByDescending {
                it.messages.maxByOrNull { it.createdAt }?.createdAt ?: it.createdAt
            }
        }
    }

    override suspend fun upsertConversationMessages(conversationId: Long, messages: List<Message>) {
        localConversationDataSource.upsertConversationMessages(
            conversationId = conversationId,
            messages = messages
        )
    }

    override suspend fun getMessageById(messageId: Long): Message? {
        return localConversationDataSource.getMessageById(messageId)
    }

    override suspend fun updateMessageStatus(conversationId: Long, messageId: Long) {
        try {
            conversationApi.updateMessageStatus(
                UpdateMessageStatusRequest(conversationId = conversationId, messageId = messageId)
            )
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    override fun getReactIcons(): Flow<List<ReactIcon>> {
        reactJob?.cancel()
        reactJob = scope.launch {
            try {
                val response = conversationApi.getReactIcons()
                if (response.isSuccessful()) {
                    response.data.forEach {
                        reactDataSource.upsert(it)
                    }
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
        return reactDataSource.findAll()
    }

    override suspend fun reactMessage(
        conversationId: Long,
        messageId: Long,
        reactId: Long,
    ): Result<Boolean> {
        return try {
            val response = conversationApi.reactMessage(
                ReactRequest(
                    conversationId = conversationId,
                    messageId = messageId,
                    reactId = reactId
                )
            )
            if (response.isSuccessful()) {
                Result.success(true)
            } else {
                throw Throwable(response.message)
            }
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    override suspend fun deleteMessage(
        conversationId: Long,
        messageId: Long,
        removeForSelfOnly: Boolean,
    ): Result<Boolean> {
        return try {
            val response = conversationApi.deleteMessage(
                conversationId = conversationId,
                messageId = messageId,
                removeForSelfOnly = removeForSelfOnly
            )
            if (response.isSuccessful()) {
                localConversationDataSource.deleteConversationMessageById(messageId)
                Result.success(true)
            } else {
                throw Throwable(response.message)
            }
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    override suspend fun getConversationAttachments(
        conversationId: Long,
        attachmentTypes: List<String>,
        page: Int,
    ): Result<List<ConversationMedia>> {
        return try {
            val response = conversationApi.getConversationAttachments(
                conversationId = conversationId,
                attachmentTypes = attachmentTypes,
                page = page,
            )
            if (response.isSuccessful()) {
                Result.success(response.data.orEmpty())
            } else {
                throw Throwable(response.message)
            }
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    override fun onDestroy() {
        streamJob?.cancel()
        scope.coroutineContext.cancel()
    }

    override fun getSearchHistories(): Flow<List<SearchKey>> {
        return searchLocalDatasource.findAllByScreen(screen = SCREEN)
    }

    override fun insertKey(text: String) {
        searchLocalDatasource.insertKey(text, screen = SCREEN)
    }

    override suspend fun clearText(text: String) {
        searchLocalDatasource.deleteByText(text, screen = SCREEN)
    }

    override suspend fun clearAll() {
        searchLocalDatasource.deleteByScreen(SCREEN)
    }

    private fun String.convertIdsToNames(participants: List<User>): String {
        return this.split(",").mapNotNull { id ->
            participants.find { it.id == id }?.name
        }.joinToString(", ")
    }

    companion object {
        private const val SCREEN = "CONVERSATION"
    }
}