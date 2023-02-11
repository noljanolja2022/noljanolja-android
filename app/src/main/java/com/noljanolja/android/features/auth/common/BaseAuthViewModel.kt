package com.noljanolja.android.features.auth.common

import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.util.RegexExt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

open class BaseAuthViewModel : BaseViewModel() {
    private val _emailFlow = MutableStateFlow("")
    val emailFlow = _emailFlow.asStateFlow()

    private val _passwordFlow = MutableStateFlow("")
    val passwordFlow = _passwordFlow.asStateFlow()

    open fun changeEmail(text: String) {
        launch {
            _emailFlow.emit(text)
        }
    }

    open fun changePassword(text: String) {
        launch {
            _passwordFlow.emit(text)
        }
    }

    fun requireValidEmail() {
        require(RegexExt.isEmailValid(emailFlow.value))
    }
}
