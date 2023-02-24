package com.noljanolja.android.features.auth.login.screen

import android.content.Intent
import com.d2brothers.firebase_auth.AuthSdk
import com.d2brothers.firebase_auth.model.AuthUser
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
class LoginViewModel @Inject constructor(
    private val navigationManager: NavigationManager,
    private val authSdk: AuthSdk,
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
                    signInWithEmailAndPassword()
                }
                LoginEvent.LoginKakao -> {
                    loginWithKakao()
                }
                LoginEvent.VerifyEmail -> {
                    val user = authSdk.getCurrentUser(true).first()
                    if (user?.isVerify == true) {
                        navigationManager.navigate(NavigationDirections.Home)
                    } else {
                        sendError(Throwable("Verify fail"))
                    }
                }
            }
        }
    }

    fun handleLoginWithGoogleFromIntent(data: Intent?) {
        launch {
            _uiStateFlow.emit(LoginUIState.Loading)
            val result = authSdk.getAccountFromGoogleIntent(data)
            handleAuthResult(result)
        }
    }

    fun handleLoginWithNaverFromIntent(data: Intent?) {
        launch {
            _uiStateFlow.emit(LoginUIState.Loading)
            val result = authSdk.getAccountFromNaverIntent(data)
            handleAuthResult(result)
        }
    }

    private fun loginWithKakao() {
        launch {
            _uiStateFlow.emit(LoginUIState.Loading)
            val result = authSdk.loginWithKakao()
            handleAuthResult(result)
        }
    }

    private fun handleAuthResult(result: Result<AuthUser>?) {
        launch {
            result?.getOrNull()?.let {
                if (it.isVerify) {
                    navigationManager.navigate(NavigationDirections.Home)
                } else {
                    _uiStateFlow.emit(LoginUIState.VerifyEmail)
                }
            } ?: result?.exceptionOrNull()?.let {
                sendError(it)
                _uiStateFlow.emit(LoginUIState.Login)
            }
        }
    }

    private fun signInWithEmailAndPassword() {
        launch {
            _uiStateFlow.emit(LoginUIState.Loading)
            try {
                requireValidEmail()
                val result = authSdk.signInWithEmailAndPassword(emailFlow.value, passwordFlow.value)
                handleAuthResult(result)
            } catch (e: ValidEmailFailed) {
                _uiStateFlow.emit(LoginUIState.Login)
                sendEmailError(e)
            } catch (e: Exception) {
                _uiStateFlow.emit(LoginUIState.Login)
                sendError(e)
            }
        }
    }

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
