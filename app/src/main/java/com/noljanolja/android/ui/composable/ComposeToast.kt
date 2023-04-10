package com.noljanolja.android.ui.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay

@Composable
fun ComposeToast(
    isVisible: Boolean,
    lifeTime: Long = 3_000,
    onDismiss: () -> Unit,
    content: @Composable BoxScope.() -> Unit,
) {
    var time = lifeTime
    LaunchedEffect(key1 = lifeTime, key2 = isVisible, block = {
        if (isVisible) {
            while (time > 0) {
                delay(1_000)
                time -= 1_000
            }
            onDismiss.invoke()
        }
    })
    if (isVisible) {
        Box(modifier = Modifier.fillMaxSize()) {
            content()
        }
    }
}