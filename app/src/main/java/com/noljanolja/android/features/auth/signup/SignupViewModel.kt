package com.noljanolja.android.features.auth.signup

import com.d2brothers.firebase_auth.AuthSdk
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.error.ValidEmailFailure
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.android.features.auth.common.BaseAuthViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject

class SignupViewModel : BaseAuthViewModel() {
    private val authSdk: AuthSdk by inject()
    private val _uiStateFlow = MutableStateFlow<SignupUIState>(SignupUIState.Agreement(listOf()))
    val uiStateFlow = _uiStateFlow.asStateFlow()

    private val _confirmPasswordFlow = MutableStateFlow("")
    val confirmPasswordFlow = _confirmPasswordFlow.asStateFlow()

    fun handleEvent(event: SignupEvent) {
        when (event) {
            is SignupEvent.ChangeConfirmPassword -> changeConfirmPassword(event.confirm)
            is SignupEvent.ChangeEmail -> changeEmail(event.email)
            is SignupEvent.ChangePassword -> changePassword(event.password)
            is SignupEvent.Signup -> onSignup()
            is SignupEvent.Next -> {
                launch {
                    when (_uiStateFlow.value) {
                        is SignupUIState.Agreement -> _uiStateFlow.emit(SignupUIState.SignupForm())
                        is SignupUIState.SignupForm -> onSignup()
                        SignupUIState.VerificationEmail -> {
//                            val user = authSdk.getCurrentUser(true).first()
//                            if (user?.isVerify == true) {
//                                navigationManager.navigate(NavigationDirections.Home)
//                            } else {
//                                sendError(Throwable("Verify fail"))
//                            }
                        }
                    }
                }
            }

            is SignupEvent.Back -> {
                launch {
                    when (_uiStateFlow.value) {
                        is SignupUIState.Agreement -> {
                            navigationManager.navigate(NavigationDirections.Auth)
                        }

                        is SignupUIState.SignupForm -> {
                            _uiStateFlow.emit(
                                SignupUIState.Agreement(
                                    listOf()
                                ),
                            )
                        }

                        SignupUIState.VerificationEmail -> {
                            _uiStateFlow.emit(SignupUIState.SignupForm())
                        }
                    }
                }
            }

            is SignupEvent.ToggleAgreement -> {
                launch {
                    val uiState = _uiStateFlow.value as? SignupUIState.Agreement ?: return@launch
                    val isEnable =
                        uiState.agreements.find { it.id == event.id }?.checked ?: return@launch
                    val newUIState = uiState.copy(
                        agreements = uiState.agreements.map { agreement ->
                            if (agreement.id == event.id) {
                                agreement.copy(checked = !isEnable)
                            } else {
                                agreement
                            }
                        },
                    )
                    _uiStateFlow.emit(newUIState)
                }
            }

            SignupEvent.ToggleAllAgreement -> {
                launch {
                    val uiState = _uiStateFlow.value as? SignupUIState.Agreement ?: return@launch
                    val isEnable = uiState.agreements.all { it.checked }
                    val newUIState = uiState.copy(
                        agreements = uiState.agreements.map { it.copy(checked = !isEnable) },
                    )
                    _uiStateFlow.emit(newUIState)
                }
            }

            is SignupEvent.GoTermsOfService -> {
                launch {
                    navigationManager.navigate(NavigationDirections.TermsOfService)
                }
            }
        }
    }

    private fun changeConfirmPassword(text: String) {
        launch {
            _confirmPasswordFlow.emit(text)
        }
    }

    private fun onSignup() {
        launch {
            try {
                _uiStateFlow.emit(SignupUIState.SignupForm(isLoading = true))
                requireValidEmail()
                val result =
                    authSdk.createUserWithEmailAndPassword(
                        emailFlow.value,
                        passwordFlow.value,
                    )
                if (result.isFailure) {
                    throw result.exceptionOrNull()!!
                } else {
                    authSdk.sendEmailVerification()
                    _uiStateFlow.emit(SignupUIState.VerificationEmail)
                }
            } catch (e: ValidEmailFailure) {
                _uiStateFlow.emit(SignupUIState.SignupForm())
                sendEmailError(e)
            } catch (e: Throwable) {
                _uiStateFlow.emit(SignupUIState.SignupForm())
                sendError(e)
            }
        }
    }
}

sealed interface SignupUIState {
    data class Agreement(val agreements: List<Agreement> = listOf()) : SignupUIState {
        data class Agreement(
            val id: String,
            val checked: Boolean,
            val tag: String,
            val description: String,
        )
    }

    data class SignupForm(val isLoading: Boolean = false) : SignupUIState
    object VerificationEmail : SignupUIState
}
