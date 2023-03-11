package com.noljanolja.android.features.home.require_login

import androidx.lifecycle.viewModelScope
import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.core.CoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class RequireLoginViewModel @Inject constructor(
    private val coreManager: CoreManager,
) : BaseViewModel() {
    val hasUser: StateFlow<Boolean> = flow {
        emit(coreManager.getCurrentUser() != null)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false,
    )
}
