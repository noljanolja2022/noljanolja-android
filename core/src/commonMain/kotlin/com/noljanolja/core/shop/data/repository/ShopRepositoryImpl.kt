package com.noljanolja.core.shop.data.repository

import com.noljanolja.core.shop.data.datasource.ShopApi
import com.noljanolja.core.shop.data.datasource.ShopLocalDatasource
import com.noljanolja.core.shop.data.model.request.BuildGiftRequest
import com.noljanolja.core.shop.data.model.request.GetGiftRequest
import com.noljanolja.core.shop.domain.model.Gift
import com.noljanolja.core.shop.domain.model.SearchKey
import com.noljanolja.core.shop.domain.repository.ShopRepository
import kotlinx.coroutines.flow.Flow

internal class ShopRepositoryImpl(
    private val shopLocalDatasource: ShopLocalDatasource,
    private val shopApi: ShopApi,
) : ShopRepository {
    override fun getSearchHistories(): Flow<List<SearchKey>> {
        return shopLocalDatasource.findAll()
    }

    override fun insertKey(text: String) {
        shopLocalDatasource.insertKey(text = text)
    }

    override suspend fun clearText(text: String) {
        shopLocalDatasource.deleteByText(text)
    }

    override suspend fun clearAll() {
        shopLocalDatasource.clearAll()
    }

    override suspend fun getGifts(searchText: String): Result<List<Gift>> {
        return try {
            val response = shopApi.getGifts(searchText)
            if (response.isSuccessful()) {
                Result.success(response.data)
            } else {
                Result.failure(Throwable(response.message))
            }
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    override suspend fun getMyGifts(): Result<List<Gift>> {
        return try {
            val response = shopApi.getMyGifts()
            if (response.isSuccessful()) {
                Result.success(response.data)
            } else {
                Result.failure(Throwable(response.message))
            }
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    override suspend fun getGiftDetail(id: Long): Result<Gift> {
        return try {
            val response = shopApi.getGiftDetail(GetGiftRequest(id))
            if (response.isSuccessful()) {
                Result.success(response.data!!)
            } else {
                Result.failure(Throwable(response.message))
            }
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    override suspend fun buyGift(id: Long): Result<Gift> {
        return try {
            val response = shopApi.buyGift(BuildGiftRequest(id))
            if (response.isSuccessful()) {
                Result.success(response.data!!)
            } else {
                Result.failure(Throwable(response.message))
            }
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }
}