package com.noljanolja.android.ui.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.noljanolja.android.common.base.UiState

@Composable
fun <D> FullSizeWithUiState(
    modifier: Modifier = Modifier,
    uiState: UiState<D>,
    error: @Composable ((Throwable?) -> Unit)? = null,
    content: @Composable (() -> Unit)? = null,
) {
    Box(modifier = modifier.fillMaxSize()) {
        content?.invoke()

        if (uiState.loading) {
            LoadingDialog()
        }
        if (uiState.error != null && error != null) {
            error.invoke(uiState.error)
        }
    }
}
