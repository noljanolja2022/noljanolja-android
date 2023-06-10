package com.noljanolja.android.features.auth.common

import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.error.ValidEmailFailure
import com.noljanolja.android.util.RegexExt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

open class BaseAuthViewModel : BaseViewModel() {
    private val _emailFlow = MutableStateFlow("")
    val emailFlow = _emailFlow.asStateFlow()

    private val _passwordFlow = MutableStateFlow("")
    val passwordFlow = _passwordFlow.asStateFlow()

    private val _emailError = MutableStateFlow<Throwable?>(null)
    val emailError = _emailError.asStateFlow()

    private val _passwordError = MutableStateFlow<Throwable?>(null)
    val passwordError = _passwordError.asStateFlow()

    open fun changeEmail(text: String) {
        launch {
            _emailFlow.emit(text)
            _emailError.emit(null)
        }
    }

    open fun changePassword(text: String) {
        launch {
            _passwordFlow.emit(text)
            _passwordError.emit(null)
        }
    }

    open fun sendEmailError(error: Throwable) {
        launch {
            _emailError.emit(error)
        }
    }

    open fun sendPasswordError(error: Throwable) {
        launch {
            _passwordError.emit(error)
        }
    }

    fun requireValidEmail() {
        try {
            require(RegexExt.isEmailValid(emailFlow.value))
        } catch (e: IllegalArgumentException) {
            throw ValidEmailFailure
        }
    }
}
