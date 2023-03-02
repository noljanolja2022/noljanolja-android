package com.noljanolja.android.ui.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun FullSizeLoading(
    showLoading: Boolean = true,
    content: @Composable (() -> Unit)? = null,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        content?.invoke()

        if (showLoading) {
            LoadingDialog()
        }
    }
}
