package com.noljanolja.android.features.auth.login_or_signup.screen

import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class LoginOrSignupViewModel @Inject constructor() : BaseViewModel() {
    private val _uiStateFlow = MutableStateFlow<LoginOrSignupUIState>(LoginOrSignupUIState.Login)
    val uiStateFlow = _uiStateFlow.asStateFlow()

    fun handleEvent(event: LoginOrSignupEvent) {
        when (event) {
            LoginOrSignupEvent.SwitchSignup -> {
                launch {
                    _uiStateFlow.emit(LoginOrSignupUIState.Signup)
                }
            }
            LoginOrSignupEvent.SwitchToLogin -> {
                launch {
                    _uiStateFlow.emit(LoginOrSignupUIState.Login)
                }
            }
        }
    }
}

enum class LoginOrSignupUIState(val index: Int) {
    Login(0),
    Signup(1)
}