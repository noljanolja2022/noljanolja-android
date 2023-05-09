package com.noljanolja.android.features.home.wallet

import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.core.loyalty.domain.model.MemberInfo
import com.noljanolja.core.user.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class WalletViewModel : BaseViewModel() {
    private val _uiStateFlow = MutableStateFlow<UiState<WalletUIData>>(UiState())
    val uiStateFlow = _uiStateFlow.asStateFlow()

    init {
        launch {
            val user = coreManager.getCurrentUser().getOrNull()
            val memberInfo = coreManager.getMemberInfo().getOrNull()
            _uiStateFlow.emit(UiState(data = WalletUIData(user = user, memberInfo = memberInfo)))
        }
    }

    fun handleEvent(event: WalletEvent) {
        launch {
            when (event) {
                WalletEvent.Setting -> {
                    navigationManager.navigate(NavigationDirections.Setting)
                }
            }
        }
    }
}

data class WalletUIData(
    val user: User?,
    val memberInfo: MemberInfo?,
    val friendNumber: Int = 100,
)