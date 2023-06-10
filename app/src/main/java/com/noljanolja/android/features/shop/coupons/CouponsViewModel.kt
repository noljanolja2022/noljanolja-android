package com.noljanolja.android.features.shop.coupons

import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.common.base.launch
import com.noljanolja.core.shop.domain.model.Gift
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CouponsViewModel : BaseViewModel() {

    private val _uiStateFlow = MutableStateFlow<UiState<CouponsUiData>>(UiState())
    val uiStateFlow = _uiStateFlow.asStateFlow()

    init {
        launch {
            val myGifts = coreManager.getMyGifts().getOrDefault(emptyList())
            _uiStateFlow.emit(
                UiState(
                    data = CouponsUiData(myGifts = myGifts)
                )
            )
        }
    }

    fun handleEvent(event: CouponsEvent) {
        launch {
            when (event) {
                CouponsEvent.Back -> back()
            }
        }
    }
}

data class CouponsUiData(
    val myGifts: List<Gift>,
)