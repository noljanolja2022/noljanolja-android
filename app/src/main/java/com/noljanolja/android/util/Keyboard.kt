package com.noljanolja.android.util

import android.view.ViewTreeObserver
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.patrykandpatrick.vico.core.extension.orZero

@Composable
fun keyboardAsState(): State<Boolean> {
    val isImeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    return rememberUpdatedState(isImeVisible)
}

@Composable
fun keyboardHeightState(): State<Int> {
    val view = LocalView.current
    val viewTreeObserver = view.viewTreeObserver
    val keyboardHeight = remember { mutableStateOf(0) }
    DisposableEffect(viewTreeObserver) {
        val listener = ViewTreeObserver.OnGlobalLayoutListener {
            val insets = ViewCompat.getRootWindowInsets(view)
            val isKeyboardOpen = insets
                ?.isVisible(WindowInsetsCompat.Type.ime()) ?: true
            keyboardHeight.value = insets?.getInsets(WindowInsetsCompat.Type.ime())
                ?.takeIf { isKeyboardOpen }?.bottom.orZero
        }
        viewTreeObserver.addOnGlobalLayoutListener(listener)
        onDispose {
            viewTreeObserver.removeOnGlobalLayoutListener(listener)
        }
    }
    return keyboardHeight
}