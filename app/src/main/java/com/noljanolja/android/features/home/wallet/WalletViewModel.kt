package com.noljanolja.android.features.home.wallet

import androidx.lifecycle.viewModelScope
import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.core.loyalty.domain.model.MemberInfo
import com.noljanolja.core.user.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn

class WalletViewModel : BaseViewModel() {
    private val _uiStateFlow = MutableStateFlow<UiState<WalletUIData>>(UiState())
    val uiStateFlow = _uiStateFlow.asStateFlow()

    val memberInfoFlow = coreManager.getMemberInfo().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MemberInfo()
    )

    init {
        launch {
            refresh()
        }
    }

    fun handleEvent(event: WalletEvent) {
        launch {
            when (event) {
                WalletEvent.Setting -> {
                    navigationManager.navigate(NavigationDirections.Setting)
                }

                WalletEvent.TransactionHistory -> {
                    navigationManager.navigate(NavigationDirections.TransactionHistory)
                }

                WalletEvent.Ranking -> {
                    navigationManager.navigate(NavigationDirections.MyRanking)
                }

                WalletEvent.Refresh -> {
                    val data = _uiStateFlow.value.data
                    _uiStateFlow.emit(
                        UiState(
                            data = data,
                            loading = true
                        )
                    )
                    refresh(forceRefresh = true)
                }
            }
        }
    }

    private suspend fun refresh(forceRefresh: Boolean = false) {
        val user = coreManager.getCurrentUser(forceRefresh = forceRefresh).getOrNull()
        _uiStateFlow.emit(UiState(data = WalletUIData(user = user)))
    }
}

data class WalletUIData(
    val user: User?,
    val friendNumber: Int = 100,
)