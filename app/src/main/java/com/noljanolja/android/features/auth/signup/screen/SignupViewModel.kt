package com.noljanolja.android.features.auth.signup.screen

import com.noljanolja.android.common.auth.domain.repository.AuthRepository
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.base.tryLaunch
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.android.common.navigation.NavigationManager
import com.noljanolja.android.features.auth.common.BaseAuthViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val navigationManager: NavigationManager
) : BaseAuthViewModel() {

    private val _uiStateFlow = MutableStateFlow<SignupUIState>(SignupUIState.Normal)
    val uiStateFlow = _uiStateFlow.asStateFlow()

    private val _confirmPasswordFlow = MutableStateFlow("")
    val confirmPasswordFlow = _confirmPasswordFlow.asStateFlow()

    fun handleEvent(event: SignupEvent) {
        when (event) {
            is SignupEvent.ChangeConfirmPassword -> changeConfirmPassword(event.confirm)
            is SignupEvent.ChangeEmail -> changeEmail(event.email)
            is SignupEvent.ChangePassword -> changePassword(event.password)
            SignupEvent.Signup -> onSignup()
        }
    }

    private fun changeConfirmPassword(text: String) {
        launch {
            _confirmPasswordFlow.emit(text)
        }
    }

    private fun onSignup() {
        tryLaunch(
            finally = {
                _uiStateFlow.emit(SignupUIState.Normal)
            }
        ) {
            _uiStateFlow.emit(SignupUIState.Loading)
            requireValidEmail()
            val result =
                authRepository.createUserWithEmailAndPassword(
                    emailFlow.value,
                    passwordFlow.value
                )
            if (result.isSuccess) {
                navigationManager.navigate(NavigationDirections.Home)
            } else {
                throw result.exceptionOrNull()!!
            }
        }
    }
}

enum class SignupUIState {
    Normal,
    Loading
}
