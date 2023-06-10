package com.noljanolja.android.features.shop.giftdetail

import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.error.UnexpectedFailure
import com.noljanolja.core.shop.domain.model.Gift
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class GiftDetailViewModel(private val giftId: Long) : BaseViewModel() {
    private val _uiStateFlow = MutableStateFlow<UiState<GiftDetailUiData>>(
        UiState(
            loading = true
        )
    )
    val uiStateFlow = _uiStateFlow.asStateFlow()

    private val _buyGiftSuccessEvent = MutableSharedFlow<Unit>()
    val buyGiftSuccessEvent = _buyGiftSuccessEvent

    init {
        launch {
            val gift = coreManager.getGiftDetail(giftId).getOrDefault(Gift())
            _uiStateFlow.emit(
                UiState(
                    data = GiftDetailUiData(gift)
                )
            )
        }
    }

    fun handleEvent(event: GiftDetailEvent) {
        launch {
            when (event) {
                GiftDetailEvent.Back -> back()
                GiftDetailEvent.Purchase -> purchase()
            }
        }
    }

    private suspend fun purchase() {
        val currentValue = _uiStateFlow.value
        _uiStateFlow.emit(
            UiState(loading = true, data = currentValue.data)
        )
        val response = coreManager.buyGift(giftId)
        if (response.getOrDefault(false)) {
            _buyGiftSuccessEvent.emit(Unit)
        } else {
            sendError(response.exceptionOrNull() ?: UnexpectedFailure)
        }
        _buyGiftSuccessEvent.emit(Unit)
        _uiStateFlow.emit(
            UiState(loading = false, data = currentValue.data)
        )
    }
}

data class GiftDetailUiData(
    val gift: Gift,
)