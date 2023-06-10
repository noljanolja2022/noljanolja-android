package com.noljanolja.core.shop.domain.repository

import com.noljanolja.core.shop.domain.model.Gift
import com.noljanolja.core.shop.domain.model.SearchKey
import kotlinx.coroutines.flow.Flow

interface ShopRepository {
    fun getSearchHistories(): Flow<List<SearchKey>>
    fun insertKey(text: String)
    suspend fun clearText(text: String)
    suspend fun clearAll()
    suspend fun getGifts(): Result<List<Gift>>
    suspend fun getMyGifts(): Result<List<Gift>>
    suspend fun getGiftDetail(id: Long): Result<Gift>
    suspend fun buyGift(id: Long): Result<Boolean>
}