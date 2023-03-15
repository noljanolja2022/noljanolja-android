package com.noljanolja.core.user.data.datasource

import com.noljanolija.core.db.ParticipantQueries
import com.noljanolija.core.db.UserQueries
import com.noljanolja.core.user.domain.model.Gender
import com.noljanolja.core.user.domain.model.User
import com.noljanolja.core.utils.transactionWithContext
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

class LocalUserDataSource(
    private val userQueries: UserQueries,
    private val participantQueries: ParticipantQueries,
    private val backgroundDispatcher: CoroutineDispatcher,
) {
    private val userMapper = { id: String,
            name: String,
            phone: String,
            email: String,
            gender: String?,
            dob: String?,
            isMe: Boolean,
            created_at: Long,
            updated_at: Long, ->
        User(
            id = id,
            name = name,
            phone = phone,
            email = email,
            gender = gender?.let { Gender.valueOf(it) },
            dob = dob?.let { LocalDate.parse(it) },
            isMe = isMe,
            createdAt = Instant.fromEpochMilliseconds(created_at),
            updatedAt = Instant.fromEpochMilliseconds(updated_at),
        )
    }

    suspend fun findById(
        id: String,
    ): User? = userQueries.findById(id, userMapper)
        .asFlow()
        .mapToOneOrNull(backgroundDispatcher)
        .firstOrNull()

    suspend fun findMe(): User? = userQueries.findMe(userMapper)
        .asFlow()
        .mapToOneOrNull(backgroundDispatcher)
        .firstOrNull()

    suspend fun findConversationParticipants(
        conversationId: Long,
        limit: Long = 0,
    ): List<User> = if (limit > 0) {
        userQueries.findByConversation(conversationId, limit, userMapper)
    } else {
        userQueries.findAllByConversation(conversationId, userMapper)
    }
        .asFlow()
        .mapToList()
        .flowOn(backgroundDispatcher)
        .firstOrNull() ?: listOf()

    suspend fun upsert(
        user: User,
    ) = userQueries.transactionWithContext(backgroundDispatcher) {
        userQueries.upsert(
            id = user.id,
            name = user.name,
            phone = user.phone.orEmpty(),
            email = user.email.orEmpty(),
            gender = user.gender?.name,
            dob = user.dob?.toString(),
            isMe = user.isMe,
            created_at = user.createdAt.toEpochMilliseconds(),
            updated_at = user.updatedAt.toEpochMilliseconds(),
        )
    }

    suspend fun upsertConversationParticipants(
        conversationId: Long,
        participants: List<User>,
    ) {
        userQueries.transactionWithContext(backgroundDispatcher) {
            participants.forEach { participant ->
                userQueries.upsert(
                    id = participant.id,
                    name = participant.name,
                    phone = participant.phone.orEmpty(),
                    email = participant.email.orEmpty(),
                    gender = participant.gender?.name,
                    dob = participant.dob?.toString(),
                    isMe = participant.isMe,
                    created_at = participant.createdAt.toEpochMilliseconds(),
                    updated_at = participant.updatedAt.toEpochMilliseconds(),
                )
                participantQueries.upsert(
                    user = participant.id,
                    conversation = conversationId,
                    created_at = Clock.System.now().toEpochMilliseconds(),
                    updated_at = Clock.System.now().toEpochMilliseconds(),
                )
            }
        }
    }

    suspend fun deleteById(
        id: String,
    ) = userQueries.transactionWithContext(backgroundDispatcher) {
        userQueries.deleteById(id)
    }

    suspend fun deleteAll() = userQueries.transactionWithContext(backgroundDispatcher) {
        userQueries.deleteAll()
    }

    suspend fun deleteAllParticipants() =
        participantQueries.transactionWithContext(backgroundDispatcher) {
            participantQueries.deleteAll()
        }
}