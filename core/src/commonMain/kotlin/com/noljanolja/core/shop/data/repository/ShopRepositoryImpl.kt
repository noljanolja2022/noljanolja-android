package com.noljanolja.core.shop.data.repository

import com.noljanolja.core.commons.*
import com.noljanolja.core.shop.data.datasource.SearchLocalDatasource
import com.noljanolja.core.shop.data.datasource.ShopApi
import com.noljanolja.core.shop.data.model.request.*
import com.noljanolja.core.shop.domain.model.Gift
import com.noljanolja.core.shop.domain.model.SearchKey
import com.noljanolja.core.shop.domain.repository.ShopRepository
import kotlinx.coroutines.flow.Flow

internal class ShopRepositoryImpl(
    private val shopLocalDatasource: SearchLocalDatasource,
    private val shopApi: ShopApi,
) : ShopRepository {
    override fun getSearchHistories(): Flow<List<SearchKey>> {
        return shopLocalDatasource.findAllByScreen(screen = SCREEN)
    }

    override fun insertKey(text: String) {
        shopLocalDatasource.insertKey(text = text, screen = SCREEN)
    }

    override suspend fun clearText(text: String) {
        shopLocalDatasource.deleteByText(text, screen = SCREEN)
    }

    override suspend fun clearAll() {
        shopLocalDatasource.deleteByScreen(SCREEN)
    }

    override suspend fun getCategories(request: GetCategoriesRequest): Result<List<ItemChoose>?>{
        return try {
            val response = shopApi.getCategories(request)
            if (response.isSuccessful()) {
                Result.success(response.data)
            } else {
                Result.failure(Throwable(response.message))
            }
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    override suspend fun getGifts(searchText: String, categoryId: String): Result<List<Gift>> {
        return try {
            val response = shopApi.getGifts(searchText, categoryId)
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

    override suspend fun getGiftDetail(id: String): Result<Gift> {
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

    override suspend fun buyGift(id: String): Result<Gift> {
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

    companion object {
        private const val SCREEN = "SHOP"
    }
}