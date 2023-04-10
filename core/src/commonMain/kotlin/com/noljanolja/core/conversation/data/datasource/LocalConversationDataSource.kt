package com.noljanolja.core.conversation.data.datasource

import com.noljanolija.core.db.ConversationQueries
import com.noljanolija.core.db.MessageQueries
import com.noljanolija.core.db.ParticipantQueries
import com.noljanolja.core.conversation.domain.model.*
import com.noljanolja.core.user.domain.model.User
import com.noljanolja.core.utils.default
import com.noljanolja.core.utils.transactionWithContext
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.datetime.Instant
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class LocalConversationDataSource(
    private val conversationQueries: ConversationQueries,
    private val messageQueries: MessageQueries,
    private val participantQueries: ParticipantQueries,
    private val backgroundDispatcher: CoroutineDispatcher,
) {
    private val json = Json.default()
    private val conversationMapper = {
            id: Long,
            title: String,
            type: String,
            creator: String,
            admin: String,
            created_at: Long,
            updated_at: Long,
        ->
        Conversation(
            id = id,
            title = title,
            type = ConversationType.valueOf(type),
            creator = User(id = creator),
            admin = User(id = admin),
            createdAt = Instant.fromEpochMilliseconds(created_at),
            updatedAt = Instant.fromEpochMilliseconds(updated_at),
        )
    }

    private val messageMapper = {
            id: Long,
            localId: String,
            conversation: Long,
            sender: String,
            message: String,
            stickerUrl: String,
            attachments: String,
            type: String,
            status: String,
            seenBy: String,
            leftParticipants: String,
            joinParticipants: String,
            created_at: Long,
            updated_at: Long,
        ->
        Message(
            id = id,
            _localId = localId,
            sender = User(id = sender),
            message = message,
            stickerUrl = stickerUrl,
            type = MessageType.valueOf(type),
            status = MessageStatus.valueOf(status),
            attachments = Json.decodeFromString(attachments),
            leftParticipants = Json.decodeFromString<List<String>>(leftParticipants)
                .map { User(name = it) },
            joinParticipants = Json.decodeFromString<List<String>>(joinParticipants)
                .map { User(name = it) },
            seenBy = Json.decodeFromString(seenBy),
            createdAt = Instant.fromEpochMilliseconds(created_at),
            updatedAt = Instant.fromEpochMilliseconds(updated_at),
        )
    }

    // Conversation

    suspend fun findAll(): Flow<List<Conversation>> =
        conversationQueries.findAll(conversationMapper).asFlow().mapToList()
            .flowOn(backgroundDispatcher)

    suspend fun findById(
        conversationId: Long,
    ): Flow<Conversation> =
        conversationQueries.findById(conversationId, conversationMapper).asFlow().mapToOne()
            .flowOn(backgroundDispatcher)

    suspend fun findSingleConversationWithUser(
        userId: String,
    ): Conversation? {
        return conversationQueries.findConversationWithUser(
            userId,
            ConversationType.SINGLE.name,
            conversationMapper
        ).asFlow().mapToOneOrNull(backgroundDispatcher).firstOrNull()
    }

    suspend fun findGroupConversationWithUsers(
        userIds: List<String>,
    ): Conversation? {
        return conversationQueries.findConversationWithUsers(
            userIds,
            ConversationType.GROUP.name,
            userIds.size.toLong(),
            conversationMapper
        ).asFlow().mapToOneOrNull(backgroundDispatcher).firstOrNull()
    }

    suspend fun upsert(
        conversation: Conversation,
    ) = conversationQueries.transactionWithContext(backgroundDispatcher) {
        conversationQueries.upsert(
            id = conversation.id,
            title = conversation.title,
            type = conversation.type.name,
            creator = conversation.creator.id,
            admin = conversation.admin.id,
            created_at = conversation.createdAt.toEpochMilliseconds(),
            updated_at = conversation.updatedAt.toEpochMilliseconds(),
        )
    }

    suspend fun deleteById(
        conversationId: Long,
    ) = conversationQueries.transactionWithContext(backgroundDispatcher) {
        conversationQueries.deleteById(conversationId)
    }

    suspend fun deleteAll() = conversationQueries.transactionWithContext(backgroundDispatcher) {
        conversationQueries.deleteAll()
    }

    // Message

    suspend fun findConversationMessages(
        conversationId: Long,
        limit: Long = 0,
    ): Flow<List<Message>> = if (limit > 0) {
        messageQueries.findByConversation(conversationId, limit, messageMapper)
    } else {
        messageQueries.findAllByConversation(conversationId, messageMapper)
    }.asFlow().mapToList().flowOn(backgroundDispatcher)

    suspend fun findConversationMessageById(
        messageId: Long,
    ): Flow<Message> = messageQueries.findById(messageId, messageMapper).asFlow().mapToOne()
        .flowOn(backgroundDispatcher)

    suspend fun upsertConversationMessages(
        conversationId: Long,
        messages: List<Message>,
    ) = messageQueries.transactionWithContext(backgroundDispatcher) {
        messages.forEach { message ->
            val existing = if (message.id != 0L) {
                messageQueries.findById(message.id, messageMapper).executeAsOneOrNull()
            } else {
                null
            }
            messageQueries.upsert(
                id = message.id,
                localId = existing?.localId ?: message.localId,
                conversation = conversationId,
                sender = message.sender.id,
                message = message.message,
                stickerUrl = message.stickerUrl,
                attachments = json.encodeToString(message.attachments),
                type = message.type.name,
                status = message.status.name,
                seenBy = json.encodeToString(message.seenBy),
                leftParticipants = json.encodeToString(message.leftParticipants.map { it.name }),
                joinParticipants = json.encodeToString(message.joinParticipants.map { it.name }),
                created_at = message.createdAt.toEpochMilliseconds(),
                updated_at = message.updatedAt.toEpochMilliseconds(),
            )
        }
    }

    suspend fun deleteConversationMessages(
        conversationId: Long,
    ) = messageQueries.transactionWithContext(backgroundDispatcher) {
        messageQueries.deleteAllByConversation(conversationId)
    }

    suspend fun deleteAllMessages() = messageQueries.transactionWithContext(backgroundDispatcher) {
        messageQueries.deleteAll()
    }

    suspend fun deleteAllConversationParticipants(
        conversationId: Long,
    ) = participantQueries.transactionWithContext(backgroundDispatcher) {
        participantQueries.deleteAllByConversation(conversationId)
    }

    suspend fun deleteConversationParticipants(
        conversationId: Long,
        userIds: List<String>,
    ) = participantQueries.transactionWithContext(backgroundDispatcher) {
        userIds.forEach {
            participantQueries.deleteByConversationUser(conversationId, it)
        }
    }

    suspend fun deleteConversationNotInIds(
        conversationIds: List<Long>,
    ) = participantQueries.transactionWithContext(backgroundDispatcher) {
        conversationQueries.deleteNotInIds(conversationIds)
    }
}