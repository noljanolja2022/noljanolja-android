package com.noljanolja.android.common.base

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.android.common.navigation.NavigationManager
import com.noljanolja.android.util.showToast
import com.noljanolja.core.CoreManager
import com.noljanolja.core.utils.defaultJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

open class BaseViewModel : ViewModel(), KoinComponent {
    protected val navigationManager: NavigationManager by inject()
    protected val coreManager: CoreManager by inject()
    protected val json = defaultJson()
    protected val context: Context by inject()

    private val _errorFlow = MutableSharedFlow<Throwable>()
    val errorFlow = _errorFlow.asSharedFlow()

    fun sendError(e: Throwable) {
        launch {
            Logger.e("${this.javaClass.name}: ViewModel show Error: $e")
            _errorFlow.emit(e)
        }
    }

    fun back() {
        launch {
            navigationManager.navigate(NavigationDirections.Back)
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

fun launchInMain(block: suspend () -> Unit) = MainScope().launch {
    block.invoke()
}

fun launchInMainIO(onError: (Throwable) -> Unit = {}, block: suspend () -> Unit) =
    MainScope().launch {
        withContext(Dispatchers.IO) {
            try {
                block.invoke()
            } catch (e: Throwable) {
                e.printStackTrace()
                onError(e)
            }
        }
    }
