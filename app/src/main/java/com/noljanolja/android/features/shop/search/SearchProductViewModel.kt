package com.noljanolja.android.features.shop.search

import androidx.lifecycle.viewModelScope
import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.launch
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class SearchProductViewModel : BaseViewModel() {
    val searchKeys = coreManager.getSearchHistories().map {
        it.sortedByDescending { it.updatedAt }.map { it.text }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun handleEvent(event: SearchProductEvent) {
        launch {
            when (event) {
                SearchProductEvent.Back -> back()
                is SearchProductEvent.Search -> coreManager.insertSearchKey(event.text)
                SearchProductEvent.ClearAll -> {
                    coreManager.clearAllSearch()
                }

                is SearchProductEvent.Clear -> {
                    coreManager.clearTextSearch(event.text)
                }
            }
        }
    }
}