package com.noljanolja.core.shop.domain.repository

import com.noljanolja.core.commons.*
import com.noljanolja.core.shop.data.model.request.*
import com.noljanolja.core.shop.domain.model.Gift
import com.noljanolja.core.shop.domain.model.SearchKey
import kotlinx.coroutines.flow.Flow

interface ShopRepository {
    fun getSearchHistories(): Flow<List<SearchKey>>
    fun insertKey(text: String)
    suspend fun clearText(text: String)
    suspend fun clearAll()
    suspend fun getCategories(request: GetCategoriesRequest): Result<List<ItemChoose>?>
    suspend fun getGifts(
        searchText: String,
        categoryId: String,
        isFeatured: Boolean? = null,
        isTodayOffer: Boolean? = null
    ): Result<List<Gift>>
    suspend fun getMyGifts(): Result<List<Gift>>
    suspend fun getGiftDetail(id: String): Result<Gift>
    suspend fun buyGift(id: String): Result<Gift>
}