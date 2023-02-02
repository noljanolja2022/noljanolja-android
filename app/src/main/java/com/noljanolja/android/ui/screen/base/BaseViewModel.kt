package com.noljanolja.android.ui.screen.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class BaseViewModel : ViewModel() {
}

fun ViewModel.launch(block: suspend () -> Unit) = viewModelScope.launch {
    block.invoke()
}