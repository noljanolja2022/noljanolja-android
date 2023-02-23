package com.noljanolja.android.features.home.menu.screen

import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.user.domain.model.User
import com.noljanolja.android.common.user.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : BaseViewModel() {
    private val _uiState = MutableStateFlow(MenuUIState())
    val uiState = _uiState.asStateFlow()

    init {
        launch {
            userRepository.getCurrentUser()?.let {
                _uiState.emit(MenuUIState(user = it))
            } ?: let {
                _uiState.emit(MenuUIState(loading = true))
                val result = userRepository.getMe()
                result.exceptionOrNull()?.let {
                    _uiState.emit(MenuUIState())
                    sendError(it)
                } ?: _uiState.emit(MenuUIState(user = result.getOrNull()))
            }
        }
    }
}

data class MenuUIState(
    val loading: Boolean = false,
    val user: User? = null,
)
