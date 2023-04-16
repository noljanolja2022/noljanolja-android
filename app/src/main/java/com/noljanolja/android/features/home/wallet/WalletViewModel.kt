package com.noljanolja.android.features.home.wallet

import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.core.user.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class WalletViewModel : BaseViewModel() {
    private val _uiStateFlow = MutableStateFlow<UiState<WalletUIData>>(UiState())
    val uiStateFlow = _uiStateFlow.asStateFlow()

    init {
        launch {
            coreManager.getCurrentUser().getOrNull()?.let {
                _uiStateFlow.emit(UiState(data = WalletUIData(user = it)))
            }
        }
    }

    fun handleEvent(event: WalletEvent) {
        launch {
            when (event) {
                WalletEvent.Logout -> {
                    if (coreManager.logout().getOrNull() == true) {
                        navigationManager.navigate(NavigationDirections.Auth)
                    }
                }
            }
        }
    }
}

data class WalletUIData(
    val user: User?,
    val friendNumber: Int = 100,
)