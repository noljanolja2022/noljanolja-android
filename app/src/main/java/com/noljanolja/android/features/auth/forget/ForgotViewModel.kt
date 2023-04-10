package com.noljanolja.android.features.auth.forget

import com.d2brothers.firebase_auth.AuthSdk
import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.navigation.NavigationCommand.FinishWithResults.Companion.FORGOT_FINISH_AUTH
import com.noljanolja.android.common.navigation.NavigationDirections
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject

class ForgotViewModel : BaseViewModel() {
    private val authSdk: AuthSdk by inject()
    private val _uiStateFlow = MutableStateFlow<ForgotUIState>(ForgotUIState.Normal("", false))
    val uiStateFlow = _uiStateFlow.asStateFlow()

    fun handleEvent(event: ForgotEvent) {
        when (event) {
            is ForgotEvent.Back -> {
                launch { navigationManager.navigate(NavigationDirections.Back) }
            }
            ForgotEvent.Close -> {
                launch {
                    navigationManager.navigate(
                        NavigationDirections.FinishWithResults(
                            mapOf(
                                FORGOT_FINISH_AUTH to true,
                            ),
                        ),
                    )
                }
            }
            is ForgotEvent.VerifyEmail -> {
                launch {
                    val uiState = _uiStateFlow.value as? ForgotUIState.Normal ?: return@launch
                    try {
                        _uiStateFlow.emit(uiState.copy(isLoading = true))
                        authSdk.sendPasswordResetEmail(uiState.email)
                        _uiStateFlow.emit(ForgotUIState.VerifyCompleted)
                    } catch (e: Throwable) {
                        _uiStateFlow.emit(uiState.copy(isLoading = false))
                        sendError(e)
                    }
                }
            }
            is ForgotEvent.ChangeEmail -> {
                launch {
                    val uiState = _uiStateFlow.value
                    (uiState as? ForgotUIState.Normal)?.let {
                        _uiStateFlow.emit(it.copy(email = event.email))
                    }
                }
            }
            is ForgotEvent.ResendPassword -> {
                launch {
                    // TODO
                }
            }
        }
    }
}

sealed interface ForgotUIState {
    data class Normal(val email: String, val isLoading: Boolean) : ForgotUIState
    object VerifyCompleted : ForgotUIState
}
