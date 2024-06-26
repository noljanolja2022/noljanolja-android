package com.noljanolja.android.features.home.menu

import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.launch
import com.noljanolja.core.user.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MenuViewModel : BaseViewModel() {
    private val _uiState = MutableStateFlow(MenuUIState())
    val uiState = _uiState.asStateFlow()

    init {
        launch {
            _uiState.emit(MenuUIState(loading = true))
            val result = coreManager.getCurrentUser()
            result.exceptionOrNull()?.let {
                _uiState.emit(MenuUIState())
                sendError(it)
            } ?: _uiState.emit(MenuUIState(user = result.getOrNull()))
        }
    }
}

data class MenuUIState(
    val loading: Boolean = false,
    val user: User? = null,
)
