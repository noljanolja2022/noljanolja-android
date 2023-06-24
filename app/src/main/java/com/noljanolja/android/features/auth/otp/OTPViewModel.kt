package com.noljanolja.android.features.auth.otp

import com.d2brothers.firebase_auth.AuthSdk
import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.navigation.NavigationDirections
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject

class OTPViewModel : BaseViewModel() {
    private val authSdk: AuthSdk by inject()
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
                    coreManager.verifyOTPCode(event.verificationId, event.otp).exceptionOrNull()
                        ?.let {
                            sendError(it)
                        }
                    authSdk.getIdToken(true)?.let {
                        coreManager.saveAuthToken(it)
                        handleAuthResult()
                    }
                }
            }
        }
    }

    private fun handleAuthResult() {
        launch {
            val result = coreManager.getCurrentUser(true)
            result.getOrNull()?.let { user ->
                if (user.name.isNullOrBlank()) {
                    navigationManager.navigate(NavigationDirections.UpdateProfile)
                } else {
                    navigationManager.navigate(NavigationDirections.Home)
                }
                coreManager.pushToken()
            } ?: result.exceptionOrNull()?.let {
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
