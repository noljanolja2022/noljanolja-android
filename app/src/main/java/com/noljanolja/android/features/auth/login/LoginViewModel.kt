package com.noljanolja.android.features.auth.login

import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.android.common.navigation.NavigationManager
import com.noljanolja.android.features.auth.common.BaseAuthViewModel
import com.noljanolja.core.user.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val navigationManager: NavigationManager,
) : BaseAuthViewModel() {
    private val _uiStateFlow = MutableStateFlow(LoginUIState.Login)
    val uiStateFlow = _uiStateFlow.asStateFlow()

    fun handleEvent(event: LoginEvent) {
        launch {
            when (event) {
                is LoginEvent.Back -> {
                    launch {
                        when (_uiStateFlow.value) {
                            LoginUIState.VerifyEmail -> {
                                _uiStateFlow.emit(LoginUIState.Login)
                            }
                            else -> navigationManager.navigate(NavigationDirections.Back)
                        }
                    }
                }
                is LoginEvent.GoJoinMember -> TODO("Not implement")
                is LoginEvent.ShowError -> {
                    event.error?.let {
                        sendError(event.error)
                    }
                }
                is LoginEvent.ChangeEmail -> {
                    changeEmail(event.email)
                }
                is LoginEvent.ChangePassword -> {
                    changePassword(event.password)
                }
                LoginEvent.GoForgotEmailAndPassword -> {
                    onForgotIdOrPassword()
                }
                LoginEvent.LoginEmail -> {
//                    signInWithEmailAndPassword()
                }
                LoginEvent.LoginKakao -> {
//                    loginWithKakao()
                }
                LoginEvent.VerifyEmail -> {}
                LoginEvent.OpenCountryList -> {
                    navigationManager.navigate(NavigationDirections.CountryPicker)
                }
                is LoginEvent.SendOTP -> {
                    navigationManager.navigate(NavigationDirections.AuthOTP(event.phone))
                }
            }
        }
    }

//    fun handleLoginWithGoogleFromIntent(data: Intent?) {
//        launch {
//            _uiStateFlow.emit(LoginUIState.Loading)
//            val result = userRepository.getAccountFromGoogleIntent(data)
//            handleAuthResult(result)
//        }
//    }
//
//    fun handleLoginWithNaverFromIntent(data: Intent?) {
//        launch {
//            _uiStateFlow.emit(LoginUIState.Loading)
//            val result = userRepository.getAccountFromNaverIntent(data)
//            handleAuthResult(result)
//        }
//    }

//    private fun loginWithKakao() {
//        launch {
//            _uiStateFlow.emit(LoginUIState.Loading)
//            val result = userRepository.loginWithKakao()
//            handleAuthResult(result)
//        }
//    }

    private fun handleAuthResult(result: Result<User>?) {
        launch {
            result?.getOrNull()?.let {
                navigationManager.navigate(NavigationDirections.Home)
//                if (it.isVerify) {
//
//                } else {
//                    _uiStateFlow.emit(LoginUIState.VerifyEmail)
//                }
            } ?: result?.exceptionOrNull()?.let {
                sendError(it)
                _uiStateFlow.emit(LoginUIState.Login)
            }
        }
    }

//    private fun signInWithEmailAndPassword() {
//        launch {
//            _uiStateFlow.emit(LoginUIState.Loading)
//            try {
//                requireValidEmail()
//                val result =
//                    userRepository.signInWithEmailAndPassword(emailFlow.value, passwordFlow.value)
//                handleAuthResult(result)
//            } catch (e: ValidEmailFailed) {
//                _uiStateFlow.emit(LoginUIState.Login)
//                sendEmailError(e)
//            } catch (e: Exception) {
//                _uiStateFlow.emit(LoginUIState.Login)
//                sendError(e)
//            }
//        }
//    }

    private fun onForgotIdOrPassword() {
        launch {
            navigationManager.navigate(NavigationDirections.Forgot)
        }
    }
}

enum class LoginUIState {
    Loading,
    Login,
    VerifyEmail,
}
