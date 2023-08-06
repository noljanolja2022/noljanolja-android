package com.noljanolja.android.features.referral

import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.common.base.launch
import com.noljanolja.core.user.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ReferralViewModel : BaseViewModel() {

    private val _uiStateFlow = MutableStateFlow<UiState<User>>(UiState())
    val uiStateFlow = _uiStateFlow.asStateFlow()

    init {
        launch {
            coreManager.getCurrentUser().getOrNull()?.let {
                _uiStateFlow.emit(UiState(data = it))
            }
        }
    }

    fun handleEvent(event: ReferralEvent) {
        launch {
            when (event) {
                ReferralEvent.Back -> back()
            }
        }
    }
}