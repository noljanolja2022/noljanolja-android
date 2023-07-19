package com.noljanolja.core.shop.data.datasource

import com.noljanolija.core.db.SearchTextQueries
import com.noljanolja.core.shop.domain.model.SearchKey
import com.noljanolja.core.utils.transactionWithContext
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

internal class SearchLocalDatasource(
    private val searchTextQueries: SearchTextQueries,
    private val backgroundDispatcher: CoroutineDispatcher,
) {
    val searchMapper = {
            text: String,
            screen: String,
            created_at: Long,
            updated_at: Long,
        ->
        SearchKey(
            text = text,
            createdAt = Instant.fromEpochMilliseconds(created_at),
            updatedAt = Instant.fromEpochMilliseconds(updated_at),
        )
    }

    fun findAllByScreen(screen: String) = searchTextQueries.findAllByScreen(
        screen,
        searchMapper
    ).asFlow()
        .mapToList(backgroundDispatcher)

    fun insertKey(text: String, screen: String) =
        searchTextQueries.upsert(
            text = text,
            screen = screen,
            created_at = Clock.System.now().toEpochMilliseconds(),
            updated_at = Clock.System.now().toEpochMilliseconds(),
        )

    suspend fun clearAll() = searchTextQueries.transactionWithContext(backgroundDispatcher) {
        searchTextQueries.deleteAll()
    }

    suspend fun deleteByText(
        text: String,
        screen: String,
    ) = searchTextQueries.transactionWithContext(backgroundDispatcher) {
        searchTextQueries.deleteByTextAndScreen(text, screen)
    }

    suspend fun deleteByScreen(screen: String) =
        searchTextQueries.transactionWithContext(backgroundDispatcher) {
            searchTextQueries.deleteAllByScreen(screen)
        }
}