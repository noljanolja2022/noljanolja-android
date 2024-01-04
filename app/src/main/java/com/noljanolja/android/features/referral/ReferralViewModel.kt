package com.noljanolja.android.features.referral

import androidx.lifecycle.*
import com.noljanolja.android.common.base.*
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.error.*
import com.noljanolja.core.contacts.domain.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class ReferralViewModel : BaseViewModel() {

    private val _referralUiState = MutableStateFlow(UiState<PointConfig>())
    val referralUiState = _referralUiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val result = coreManager.getPointConfig()
            if (result.isSuccess) {
                _referralUiState.emit(
                    UiState(
                        data = result.getOrNull()
                    )
                )
            } else {
                sendError(result.exceptionOrUnDefined())
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