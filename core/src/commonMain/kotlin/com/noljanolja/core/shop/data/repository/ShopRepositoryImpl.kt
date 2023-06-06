package com.noljanolja.core.shop.data.repository

import com.noljanolja.core.shop.data.datasource.ShopLocalDatasource
import com.noljanolja.core.shop.domain.model.SearchKey
import com.noljanolja.core.shop.domain.repository.ShopRepository
import kotlinx.coroutines.flow.Flow

internal class ShopRepositoryImpl(
    private val shopLocalDatasource: ShopLocalDatasource,
) : ShopRepository {
    override fun getLocalSearchs(): Flow<List<SearchKey>> {
        return shopLocalDatasource.findAll()
    }

    override fun insertKey(text: String) {
        shopLocalDatasource.insertKey(text = text)
    }
}