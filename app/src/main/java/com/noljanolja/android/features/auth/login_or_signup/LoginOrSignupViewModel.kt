package com.noljanolja.android.features.auth.login_or_signup

import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.navigation.NavigationDirections
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoginOrSignupViewModel : BaseViewModel() {
    private val _uiStateFlow = MutableStateFlow(LoginOrSignupUIState.Login)
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
            LoginOrSignupEvent.Back -> {
                launch {
                    if (_uiStateFlow.value == LoginOrSignupUIState.Signup) {
                        _uiStateFlow.emit(LoginOrSignupUIState.Login)
                    } else {
                        navigationManager.navigate(NavigationDirections.Back)
                    }
                }
            }
            LoginOrSignupEvent.Close -> {
                launch {
                    navigationManager.navigate(NavigationDirections.Back)
                }
            }
        }
    }
}

enum class LoginOrSignupUIState(val index: Int) {
    Login(0),
    Signup(1),
}
