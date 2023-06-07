package com.noljanolja.android.features.shop.giftdetail

import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.common.base.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class GiftDetailViewModel : BaseViewModel() {
    private val _uiStateFlow = MutableStateFlow<UiState<GiftDetailUiData>>(UiState())
    val uiStateFlow = _uiStateFlow.asStateFlow()

    fun handleEvent(event: GiftDetailEvent) {
        launch {
            when (event) {
                GiftDetailEvent.Back -> back()
            }
        }
    }
}

data class GiftDetailUiData(
    val name: String,
)