package com.noljanolja.android.features.home.wallet.myranking

import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.common.base.launch
import com.noljanolja.core.loyalty.domain.model.MemberInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MyRankingViewModel : BaseViewModel() {
    private val _uiStateFlow = MutableStateFlow<UiState<MyRankingUiData>>(UiState(loading = true))
    val uiStateFlow = _uiStateFlow.asStateFlow()

    init {
        launch {
            coreManager.getMemberInfo().collect {
                _uiStateFlow.emit(UiState(data = MyRankingUiData(memberInfo = it)))
            }
        }
    }

    fun handleEvent(event: MyRankingEvent) {
        launch {
            when (event) {
                MyRankingEvent.Back -> back()
            }
        }
    }
}

data class MyRankingUiData(
    val memberInfo: MemberInfo,
)