package com.noljanolja.android.features.home.wallet.detail

import androidx.lifecycle.*
import com.noljanolja.android.common.base.*
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.extensions.*
import com.noljanolja.android.features.home.wallet.model.*
import com.noljanolja.core.loyalty.data.model.request.*
import com.noljanolja.core.loyalty.domain.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class TransactionDetailViewModel(
    loyaltyPointId: String = "",
    reason: String = "",
) : BaseViewModel() {
    private val _uiStateFlow = MutableStateFlow(UiLoyaltyPoint())
    internal val uiStateFlow = _uiStateFlow.asStateFlow()

    init {
        viewModelScope.launch {
            callMultipleApisOnThread(
                requests = listOf(
                    BaseFunCallAPI(
                        key = "",
                        funCallAPI = {
                            coreManager.getLoyaltyPointDetail(
                                GetLoyaltyPointDetailRequest(
                                    id = loyaltyPointId,
                                    reason = reason
                                )
                            )
                        }
                    )
                ),
                onEachSuccess = { data, _ ->
                    data.castTo<LoyaltyPoint>()?.run {
                        _uiStateFlow.emit(toUiModel())
                    }
                }
            )
        }
    }

    fun handleEvent(event: TransactionDetailEvent) {
        launch {
            when (event) {
                TransactionDetailEvent.Back -> back()
            }
        }
    }
}