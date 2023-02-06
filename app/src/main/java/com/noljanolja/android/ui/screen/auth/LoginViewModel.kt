package com.noljanolja.android.ui.screen.auth

import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.noljanolja.android.data.repositories.AuthRepository
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
) : ViewModel() {
    fun handleEvent(event: LoginEvent) {
        when (event) {
            LoginEvent.GoToMain -> {
                launch {
                    navigationManager.navigate(NavigationDirections.Home)
                }
            }
        }
    }

    fun loginWithKakao() {
        launch {
            val result = authRepository.loginWithKakao()
            if (result.isSuccess) {
                handleEvent(LoginEvent.GoToMain)
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
            // TODO
        }
    }
}
