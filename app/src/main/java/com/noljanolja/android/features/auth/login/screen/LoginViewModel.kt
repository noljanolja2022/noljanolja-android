package com.noljanolja.android.features.auth.login.screen

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.noljanolja.android.common.auth.domain.repository.AuthRepository
import com.noljanolja.android.common.base.launch
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

    private val _uiStateFlow = MutableStateFlow<LoginUIState>(LoginUIState.None)
    val uiStateFlow = _uiStateFlow.asStateFlow()
    fun handleEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.GoToMain -> {
                launch {
                    navigationManager.navigate(NavigationDirections.Home)
                }
            }
            is LoginEvent.GoToSignup -> {
                launch {
                    navigationManager.navigate(NavigationDirections.Signup)
                }
            }
            is LoginEvent.ShowError -> {
                event.error?.let {
                    showError(event.error)
                }
            }
        }
    }

    fun loginWithKakao() {
        launch {
            _uiStateFlow.emit(LoginUIState.Loading)
            val result = authRepository.loginWithKakao()
            if (result.isSuccess) {
                handleEvent(LoginEvent.GoToMain)
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
                handleEvent(LoginEvent.GoToMain)
            } catch (e: ApiException) {
                showError(e)
            } finally {
                _uiStateFlow.emit(LoginUIState.None)
            }
        }
    }

    fun signInWithEmailAndPassword() {
        launch {
            _uiStateFlow.emit(LoginUIState.None)
            try {
                requireValidEmail()
                val result =
                    authRepository.signInWithEmailAndPassword(emailFlow.value, passwordFlow.value)
                if (result.isSuccess) {
                    navigationManager.navigate(NavigationDirections.Home)
                }
            } catch (e: Exception) {
                // TODO
            } finally {
                _uiStateFlow.emit(LoginUIState.None)
            }
        }
    }

    fun goToSignup() {
        handleEvent(LoginEvent.GoToSignup)
    }
}

sealed interface LoginUIState {
    object Loading : LoginUIState
    object None : LoginUIState
}
