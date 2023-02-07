package com.noljanolja.android

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noljanolja.android.common.domain.model.User
import com.noljanolja.android.common.domain.repositories.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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
        delay(1500)
        MainActivityUiState.Success(it)
    }.stateIn(
        scope = viewModelScope,
        initialValue = MainActivityUiState.Loading,
        started = SharingStarted.Eagerly
    )
}

sealed interface MainActivityUiState {
    object Loading : MainActivityUiState
    data class Success(val user: User?) : MainActivityUiState
}
