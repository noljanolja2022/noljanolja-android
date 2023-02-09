package com.noljanolja.android

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noljanolja.android.common.auth.domain.model.User
import com.noljanolja.android.common.auth.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val uiState: StateFlow<MainActivityUiState> = authRepository.getCurrentUser().map {
        MainActivityUiState.Success(it)
    }.stateIn(
        scope = viewModelScope,
        initialValue = MainActivityUiState.Loading,
        started = SharingStarted.WhileSubscribed(5_000)
    )
}

sealed interface MainActivityUiState {
    object Loading : MainActivityUiState
    data class Success(val user: User?) : MainActivityUiState
}
