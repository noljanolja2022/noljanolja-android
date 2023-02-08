package com.noljanolja.android.features.auth.signup.screen

import com.noljanolja.android.common.auth.domain.repository.AuthRepository
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.android.common.navigation.NavigationManager
import com.noljanolja.android.features.auth.common.BaseAuthViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val navigationManager: NavigationManager
) : BaseAuthViewModel() {

    fun onSignup() {
        launch {
            try {
                requireValidEmail()
                val result =
                    authRepository.createUserWithEmailAndPassword(
                        emailFlow.value,
                        passwordFlow.value
                    )
                if (result.isSuccess) {
                    navigationManager.navigate(NavigationDirections.Home)
                }
            } catch (e: Exception) {
                // TODO
            }
        }
    }
}
