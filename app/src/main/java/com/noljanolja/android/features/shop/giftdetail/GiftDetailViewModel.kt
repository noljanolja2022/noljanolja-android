package com.noljanolja.android.features.shop.giftdetail

import com.noljanolja.android.*
import com.noljanolja.android.common.base.*
import com.noljanolja.android.common.error.*
import com.noljanolja.android.common.navigation.*
import com.noljanolja.core.exchange.domain.domain.*
import com.noljanolja.core.shop.data.model.request.*
import com.noljanolja.core.shop.domain.model.*
import kotlinx.coroutines.flow.*

class GiftDetailViewModel(
    private val giftId: String,
    private val code: String,
) : BaseViewModel() {
    private val _uiStateFlow = MutableStateFlow<UiState<GiftDetailUiData>>(
        UiState(
            loading = true
        )
    )
    val uiStateFlow = _uiStateFlow.asStateFlow()

    private val _myBalanceFlow = MutableStateFlow(ExchangeBalance())

    val myBalanceFlow = _myBalanceFlow.asStateFlow()

    private val _buyGiftSuccessEvent = MutableSharedFlow<Boolean>()
    val buyGiftSuccessEvent = _buyGiftSuccessEvent

    init {
        launch {
            val gift = coreManager.getGiftDetail(giftId).getOrDefault(Gift())
            val giftsByCategory =
                coreManager.getGifts(
                    GetGiftListRequest(
                        categoryId = gift.category.id,
                        locale = MyApplication.localeSystem
                    )
                ).getOrDefault(emptyList())
                    .toMutableList()
            giftsByCategory.remove(gift)

            _uiStateFlow.emit(
                UiState(
                    data = GiftDetailUiData(
                        gift = gift.copy(qrCode = code),
                        giftsByCategory = giftsByCategory
                    )
                )
            )
        }
        launch {
            coreManager.getExchangeBalance().getOrNull()?.let {
                _myBalanceFlow.emit(it)
            }
        }
    }

    fun handleEvent(event: GiftDetailEvent) {
        launch {
            when (event) {
                GiftDetailEvent.Back -> back()

                GiftDetailEvent.Purchase -> purchase()

                is GiftDetailEvent.GiftDetail -> navigationManager.navigate(
                    NavigationDirections.GiftDetail(event.giftId, event.code)
                )
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
            val giftsByCategory =
                coreManager.getGifts(
                    GetGiftListRequest(
                        categoryId = it.category.id,
                        locale = MyApplication.localeSystem
                    )
                ).getOrDefault(emptyList()).toMutableList()
            giftsByCategory.remove(it)
            _buyGiftSuccessEvent.emit(true)
            _uiStateFlow.emit(
                UiState(
                    data = GiftDetailUiData(
                        gift = it,
                        giftsByCategory = giftsByCategory
                    )
                )
            )
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
    val giftsByCategory: MutableList<Gift>
)