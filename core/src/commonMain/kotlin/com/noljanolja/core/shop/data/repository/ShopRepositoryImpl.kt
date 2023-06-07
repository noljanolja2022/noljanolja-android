package com.noljanolja.core.shop.data.repository

import com.noljanolja.core.shop.data.datasource.ShopLocalDatasource
import com.noljanolja.core.shop.domain.model.SearchKey
import com.noljanolja.core.shop.domain.repository.ShopRepository
import kotlinx.coroutines.flow.Flow

internal class ShopRepositoryImpl(
    private val shopLocalDatasource: ShopLocalDatasource,
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
}