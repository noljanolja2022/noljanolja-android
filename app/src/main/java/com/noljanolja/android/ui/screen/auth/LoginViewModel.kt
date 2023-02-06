package com.noljanolja.android.ui.screen.auth

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.noljanolja.android.domain.repositories.AuthRepository
import com.noljanolja.android.ui.screen.base.BaseViewModel
import com.noljanolja.android.ui.screen.base.launch
import com.noljanolja.android.ui.screen.navigation.NavigationDirections
import com.noljanolja.android.ui.screen.navigation.NavigationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val navigationManager: NavigationManager,
    private val authRepository: AuthRepository
) : BaseViewModel() {
    fun handleEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.GoToMain -> {
                launch {
                    navigationManager.navigate(NavigationDirections.Home)
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
            val result = authRepository.loginWithKakao()
            if (result.isSuccess) {
                handleEvent(LoginEvent.GoToMain)
            } else {
                handleEvent(LoginEvent.ShowError(result.exceptionOrNull()))
            }
        }
    }

    fun getGoogleIntent() = authRepository.getGoogleSignInIntent()

    fun handleLoginGoogleResult(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            launch {
                val authResult = Firebase.auth.signInWithCredential(credential).await()
                handleEvent(LoginEvent.GoToMain)
            }
        } catch (e: ApiException) {
            showError(e)
        }
    }
}
