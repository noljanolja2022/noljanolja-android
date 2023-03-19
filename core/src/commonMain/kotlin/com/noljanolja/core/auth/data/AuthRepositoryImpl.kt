package com.noljanolja.core.auth.data

import com.noljanolija.core.db.AuthQueries
import com.noljanolja.core.auth.domain.repository.AuthRepository
import com.noljanolja.core.utils.transactionWithContext
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

internal class AuthRepositoryImpl(
    private val tokenQueries: AuthQueries,
    private val backgroundDispatcher: CoroutineDispatcher,
) : AuthRepository {

    override suspend fun getAuthToken() = tokenQueries.find()
        .asFlow()
        .mapToOneOrNull(backgroundDispatcher)
        .map { it?.auth_token }

    override suspend fun saveAuthToken(
        authToken: String,
    ) = tokenQueries.transactionWithContext(backgroundDispatcher) {
        tokenQueries.upsertAuthToken(authToken, Clock.System.now().toEpochMilliseconds())
    }

    override suspend fun getPushToken() = tokenQueries.find()
        .asFlow()
        .mapToOneOrNull(backgroundDispatcher)
        .firstOrNull()?.push_token

    override suspend fun savePushToken(
        pushToken: String,
    ) = tokenQueries.transactionWithContext(backgroundDispatcher) {
        tokenQueries.upsertPushToken(pushToken, Clock.System.now().toEpochMilliseconds())
    }

    override suspend fun delete() = tokenQueries.transactionWithContext(backgroundDispatcher) {
        tokenQueries.delete()
    }
}