package com.noljanolja.android.features.auth.otp

import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.android.common.navigation.NavigationManager
import com.noljanolja.android.common.user.domain.model.User
import com.noljanolja.android.common.user.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class OTPViewModel @Inject constructor(
    private val navigationManager: NavigationManager,
    private val userRepository: UserRepository,
) : BaseViewModel() {
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
                    val result = userRepository.verifyOTPCode(event.verificationId, event.otp)
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
