package com.noljanolja.android.ui.composable

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.noljanolja.android.common.base.UiState

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <D> ScaffoldWithUiState(
    modifier: Modifier = Modifier,
    uiState: UiState<D>,
    topBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    error: @Composable ((Throwable?) -> Unit)? = null,
    content: @Composable (() -> Unit)? = null,
) {
    Scaffold(
        modifier = modifier,
        topBar = topBar,
        floatingActionButton = floatingActionButton,
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(it)) {
            content?.invoke()
        }
        LoadingDialog(isLoading = uiState.loading)
        if (uiState.error != null && error != null) {
            error.invoke(uiState.error)
        }
    }
}
