package com.noljanolja.android.features.home.require_login

import androidx.lifecycle.viewModelScope
import com.noljanolja.android.common.base.BaseViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class RequireLoginViewModel : BaseViewModel() {
    val hasUser: StateFlow<Boolean> = flow {
        emit(coreManager.getCurrentUser() != null)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false,
    )
}
