package com.noljanolja.android.common.base

import android.content.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.*
import androidx.lifecycle.*
import co.touchlab.kermit.*
import com.noljanolja.android.common.navigation.*
import com.noljanolja.android.util.*
import com.noljanolja.core.*
import com.noljanolja.core.utils.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.koin.core.component.*

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

    open fun <Any> callMultipleApisOnThread(
        requests: List<BaseFunCallAPI<out Any>>,
        onEachSuccess: (Any?, String) -> Unit = { _, _ -> },
        onEachError: (String) -> Unit = { _ -> },
        onFinish: suspend () -> Unit = {}
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            requests.map { request ->
                async {
                    request.run {
                        val result = funCallAPI.invoke()
                        if (result.isSuccess) {
                            onEachSuccess(
                                result.getOrDefault(null),
                                key
                            )
                        } else {
                            onEachError(key)
                        }
                    }
                }
            }.awaitAll()
            onFinish()
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
