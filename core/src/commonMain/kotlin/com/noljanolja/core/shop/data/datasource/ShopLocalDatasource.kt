package com.noljanolja.core.shop.data.datasource

import com.noljanolija.core.db.SearchTextQueries
import com.noljanolja.core.shop.domain.model.SearchKey
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

internal class ShopLocalDatasource(
    private val searchTextQueries: SearchTextQueries,
    private val backgroundDispatcher: CoroutineDispatcher,
) {
    val searchMapper = {
            text: String, created_at: Long,
            updated_at: Long,
        ->
        SearchKey(
            text = text,
            createdAt = Instant.fromEpochMilliseconds(created_at),
            updatedAt = Instant.fromEpochMilliseconds(updated_at),
        )
    }

    fun findAll() = searchTextQueries.findAll(searchMapper).asFlow().mapToList(backgroundDispatcher)

    fun insertKey(text: String) =
        searchTextQueries.upsert(
            text = text,
            created_at = Clock.System.now().toEpochMilliseconds(),
            updated_at = Clock.System.now().toEpochMilliseconds(),
        )
}