package com.noljanolja.android.features.auth.otp

import com.d2brothers.firebase_auth.*
import com.noljanolja.android.common.base.*
import com.noljanolja.android.common.navigation.*
import kotlinx.coroutines.flow.*
import org.koin.core.component.*

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
                    _uiStateFlow.run {
                        emit(value.copy(loading = true))
                    }
                    coreManager.verifyOTPCode(event.verificationId, event.otp).exceptionOrNull()
                        ?.let {
                            sendError(it)
                        }
                    authSdk.getIdToken(true)?.let {
                        coreManager.saveAuthToken(it)
                        handleAuthResult()
                    } ?: _uiStateFlow.run {
                        emit(value.copy(loading = false))
                    }
                }
            }
        }
    }

    private fun handleAuthResult() {
        launch {
            val result = coreManager.getCurrentUser(true)
            _uiStateFlow.run {
                emit(value.copy(loading = false))
            }
            result.getOrNull()?.let { user ->
                if (user.name.isBlank()) {
                    navigationManager.navigate(NavigationDirections.TermsOfService)
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
