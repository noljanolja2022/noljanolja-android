package com.noljanolja.android.common.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noljanolja.android.util.showToast
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

open class BaseViewModel : ViewModel() {
    private val _errorFlow = MutableSharedFlow<Throwable>()
    val errorFlow = _errorFlow.asSharedFlow()

    fun sendError(e: Throwable) {
        launch {
            _errorFlow.emit(e)
        }
    }
}

fun ViewModel.launch(block: suspend () -> Unit) = viewModelScope.launch {
    block.invoke()
}

fun BaseViewModel.tryLaunch(
    catch: (suspend () -> Unit)? = null,
    finally: (suspend () -> Unit)? = null,
    block: suspend () -> Unit,
) = viewModelScope.launch {
    try {
        block.invoke()
    } catch (e: Throwable) {
        sendError(e)
        catch?.invoke()
    } finally {
        finally?.invoke()
    }
}

@Composable
fun BaseViewModel.handleError() {
    val context = LocalContext.current
    LaunchedEffect(key1 = errorFlow) {
        errorFlow.collect {
            context.showToast(it.message)
        }
    }
}
