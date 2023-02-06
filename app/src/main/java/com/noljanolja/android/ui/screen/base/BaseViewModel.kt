package com.noljanolja.android.ui.screen.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

open class BaseViewModel : ViewModel() {
    private val _errorFlow = MutableSharedFlow<Throwable>()
    val errorFlow = _errorFlow.asSharedFlow()

    fun showError(e: Throwable) {
        launch {
            _errorFlow.emit(e)
        }
    }
}

fun ViewModel.launch(block: suspend () -> Unit) = viewModelScope.launch {
    block.invoke()
}