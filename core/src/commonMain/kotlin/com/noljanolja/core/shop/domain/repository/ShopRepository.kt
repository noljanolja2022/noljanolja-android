package com.noljanolja.core.shop.domain.repository

import com.noljanolja.core.shop.domain.model.SearchKey
import kotlinx.coroutines.flow.Flow

interface ShopRepository {
    fun getSearchHistories(): Flow<List<SearchKey>>
    fun insertKey(text: String)

    suspend fun clearText(text: String)

    suspend fun clearAll()
}