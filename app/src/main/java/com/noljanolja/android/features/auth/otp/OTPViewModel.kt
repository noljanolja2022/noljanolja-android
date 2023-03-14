package com.noljanolja.android.features.auth.otp

import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.core.user.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class OTPViewModel : BaseViewModel() {
    private val _uiStateFlow: MutableStateFlow<OTPUIState> = MutableStateFlow(OTPUIState())
    val uiStateFlow: StateFlow<OTPUIState> = _uiStateFlow.asStateFlow()

    fun handleEvent(event: OTPEvent) {
        launch {
            when (event) {
                is OTPEvent.DismissError -> {
                    with(_uiStateFlow) {
                        emit(value.copy(error = null))
                    }
                }
                is OTPEvent.VerifyOTP -> {
                    val result = coreManager.verifyOTPCode(event.verificationId, event.otp)
                    handleAuthResult(result)
                }
            }
        }
    }

    private fun handleAuthResult(result: Result<User>?) {
        launch {
            result?.getOrNull()?.let { user ->
                if (user.name.isNullOrBlank()) {
                    navigationManager.navigate(NavigationDirections.UpdateProfile)
                } else {
                    navigationManager.navigate(NavigationDirections.Home)
                }
            } ?: result?.exceptionOrNull()?.let {
                sendError(it)
                _uiStateFlow.emit(OTPUIState(error = it))
            }
        }
    }
}

data class OTPUIState(
    val loading: Boolean = false,
    val error: Throwable? = null,
)
