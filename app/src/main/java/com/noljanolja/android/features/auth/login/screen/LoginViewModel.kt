package com.noljanolja.android.features.auth.login.screen

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.noljanolja.android.common.auth.domain.repository.AuthRepository
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.error.ValidEmailFailed
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.android.common.navigation.NavigationManager
import com.noljanolja.android.features.auth.common.BaseAuthViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val navigationManager: NavigationManager,
    private val authRepository: AuthRepository
) : BaseAuthViewModel() {

    private val _uiStateFlow = MutableStateFlow(LoginUIState.None)
    val uiStateFlow = _uiStateFlow.asStateFlow()

    fun handleEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.Back -> {
                launch {
                    navigationManager.navigate(NavigationDirections.Back)
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
            is LoginEvent.LoginNaver -> {
                loginWithNaver(event.token)
            }
        }
    }

    private fun loginWithKakao() {
        launch {
            _uiStateFlow.emit(LoginUIState.Loading)
            val result = authRepository.loginWithKakao()
            if (result.isSuccess) {
                finishLogin()
            } else {
                handleEvent(LoginEvent.ShowError(result.exceptionOrNull()))
            }
            _uiStateFlow.emit(LoginUIState.None)
        }
    }

    private fun loginWithNaver(token: String) {
        launch {
            _uiStateFlow.emit(LoginUIState.Loading)
            val result = authRepository.loginWithNaver(token)
            if (result.isSuccess) {
                finishLogin()
            } else {
                handleEvent(LoginEvent.ShowError(result.exceptionOrNull()))
            }
            _uiStateFlow.emit(LoginUIState.None)
        }
    }


    fun getGoogleIntent() = authRepository.getGoogleSignInIntent()

    fun handleLoginGoogleResult(task: Task<GoogleSignInAccount>) {
        launch {
            _uiStateFlow.emit(LoginUIState.Loading)
            try {
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                Firebase.auth.signInWithCredential(credential).await()
                finishLogin()
            } catch (e: ApiException) {
                sendError(e)
            } finally {
                _uiStateFlow.emit(LoginUIState.None)
            }
        }
    }

    private fun signInWithEmailAndPassword() {
        launch {
            _uiStateFlow.emit(LoginUIState.Loading)
            try {
                requireValidEmail()
                val result =
                    authRepository.signInWithEmailAndPassword(emailFlow.value, passwordFlow.value)
                result.exceptionOrNull()?.let {
                    throw it
                }
                finishLogin()
            } catch (e: ValidEmailFailed) {
                sendEmailError(e)
            } catch (e: Exception) {
                sendError(e)
            } finally {
                _uiStateFlow.emit(LoginUIState.None)
            }
        }
    }

    private fun onForgotIdOrPassword() {
        launch {
            navigationManager.navigate(NavigationDirections.Forgot)
        }
    }

    private fun finishLogin() {
        launch {
            navigationManager.navigate(NavigationDirections.Back)
        }
    }
}

enum class LoginUIState {
    Loading,
    None
}
