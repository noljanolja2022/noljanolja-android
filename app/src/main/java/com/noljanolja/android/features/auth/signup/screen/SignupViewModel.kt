package com.noljanolja.android.features.auth.signup.screen

import com.noljanolja.android.common.auth.domain.repository.AuthRepository
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.error.ValidEmailFailed
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.android.common.navigation.NavigationManager
import com.noljanolja.android.features.auth.common.BaseAuthViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val navigationManager: NavigationManager
) : BaseAuthViewModel() {

    private val _uiStateFlow = MutableStateFlow<SignupUIState>(SignupUIState.Agreement(AGREEMENTS))
    val uiStateFlow = _uiStateFlow.asStateFlow()

    private val _confirmPasswordFlow = MutableStateFlow("")
    val confirmPasswordFlow = _confirmPasswordFlow.asStateFlow()

//    init {
//        launch {
//            val currentUser = authRepository.getCurrentUser().first() ?: return@launch
//            if (!currentUser.isVerify) {
//                _uiStateFlow.emit(SignupUIState.VerificationEmail)
//            }
//        }
//    }

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
                            val user = authRepository.getCurrentUser().first()
                            if (user?.isVerify == true) {
                                navigationManager.navigate(NavigationDirections.Home)
                            } else {
                                sendError(Throwable("Verify fail"))
                            }
                        }
                    }
                }
            }
            is SignupEvent.Back -> {
                launch {
                    when (_uiStateFlow.value) {
                        is SignupUIState.Agreement -> {
                            navigationManager.navigate(NavigationDirections.LoginOrSignup)
                        }
                        is SignupUIState.SignupForm -> {
                            _uiStateFlow.emit(
                                SignupUIState.Agreement(
                                    AGREEMENTS.map { it.copy(checked = true) }
                                )
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
                        }
                    )
                    _uiStateFlow.emit(newUIState)
                }
            }
            SignupEvent.ToggleAllAgreement -> {
                launch {
                    val uiState = _uiStateFlow.value as? SignupUIState.Agreement ?: return@launch
                    val isEnable = uiState.agreements.all { it.checked }
                    val newUIState = uiState.copy(
                        agreements = uiState.agreements.map { it.copy(checked = !isEnable) }
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
                    authRepository.createUserWithEmailAndPassword(
                        emailFlow.value,
                        passwordFlow.value
                    )
                if (result.isFailure) {
                    throw result.exceptionOrNull()!!
                } else {
                    authRepository.sendEmailVerification()
                    _uiStateFlow.emit(SignupUIState.VerificationEmail)
                }
            } catch (e: ValidEmailFailed) {
                _uiStateFlow.emit(SignupUIState.SignupForm())
                sendEmailError(e)
            } catch (e: Exception) {
                _uiStateFlow.emit(SignupUIState.SignupForm())
                sendError(e)
            }
        }
    }

    companion object {
        val AGREEMENTS = listOf(
            SignupUIState.Agreement.Agreement(
                id = "1",
                true,
                tag = "[Essential]",
                description = "Subscribe Terms of Service"
            ),
            SignupUIState.Agreement.Agreement(
                id = "2",
                true,
                tag = "[Essential]",
                description = "You are 14 years old or older."
            ),
            SignupUIState.Agreement.Agreement(
                id = "3",
                true,
                tag = "[Essential]",
                description = "Collection, Use and Third Parties of Personal Information consent to provide"
            ),
            SignupUIState.Agreement.Agreement(
                id = "4",
                true,
                tag = "[Select]",
                description = "Consent to receive marketing information"
            )
        )
    }
}

sealed interface SignupUIState {
    data class Agreement(val agreements: List<Agreement> = listOf()) : SignupUIState {
        data class Agreement(
            val id: String,
            val checked: Boolean,
            val tag: String,
            val description: String
        )
    }

    data class SignupForm(val isLoading: Boolean = false) : SignupUIState
    object VerificationEmail : SignupUIState
}
