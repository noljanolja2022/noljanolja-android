package com.noljanolja.android.features.home.require_login

import androidx.lifecycle.viewModelScope
import com.noljanolja.android.common.base.BaseViewModel
import kotlinx.coroutines.flow.*

class RequireLoginViewModel : BaseViewModel() {
    val hasUser: StateFlow<Boolean> = flow {
        emit(coreManager.getCurrentUser() != null)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false,
    )
}
