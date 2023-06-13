package com.noljanolja.android.features.shop.giftdetail

import androidx.lifecycle.viewModelScope
import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.base.launchInMain
import com.noljanolja.android.common.error.UnexpectedFailure
import com.noljanolja.core.loyalty.domain.model.MemberInfo
import com.noljanolja.core.shop.domain.model.Gift
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn

class GiftDetailViewModel(
    private val giftId: Long,
    private val code: String,
) : BaseViewModel() {
    private val _uiStateFlow = MutableStateFlow<UiState<GiftDetailUiData>>(
        UiState(
            loading = true
        )
    )
    val uiStateFlow = _uiStateFlow.asStateFlow()

    val memberInfoFlow = coreManager.getMemberInfo().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MemberInfo()
    )

    private val _buyGiftSuccessEvent = MutableSharedFlow<Boolean>()
    val buyGiftSuccessEvent = _buyGiftSuccessEvent

    init {
        launch {
            val gift = coreManager.getGiftDetail(giftId).getOrDefault(Gift())
            _uiStateFlow.emit(
                UiState(
                    data = GiftDetailUiData(gift.copy(code = code))
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
        response.getOrNull()?.let {
            _buyGiftSuccessEvent.emit(true)
            _uiStateFlow.emit(UiState(data = GiftDetailUiData(it)))
            launchInMain {
                coreManager.refreshMemberInfo()
            }
        } ?: let {
            sendError(response.exceptionOrNull() ?: UnexpectedFailure)
            _uiStateFlow.emit(
                UiState(loading = false, data = currentValue.data)
            )
        }
    }
}

data class GiftDetailUiData(
    val gift: Gift,
)