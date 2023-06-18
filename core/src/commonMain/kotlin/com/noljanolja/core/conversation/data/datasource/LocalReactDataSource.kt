package com.noljanolja.core.conversation.data.datasource

import com.noljanolija.core.db.ReactQueries
import com.noljanolja.core.conversation.domain.model.ReactIcon
import com.noljanolja.core.utils.transactionWithContext
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class LocalReactDataSource(
    private val reactQueries: ReactQueries,
    private val backgroundDispatcher: CoroutineDispatcher,
) {
    private val reactMapper = { id: Long, code: String, description: String? ->
        ReactIcon(
            id = id,
            code = code,
            description = description.orEmpty()
        )
    }

    fun findAll(): Flow<List<ReactIcon>> =
        reactQueries.findAll(reactMapper).asFlow().mapToList()
            .flowOn(backgroundDispatcher)

    suspend fun upsert(react: ReactIcon) {
        reactQueries.transactionWithContext(backgroundDispatcher) {
            reactQueries.upsert(code = react.code, id = react.id, description = react.description)
        }
    }
}