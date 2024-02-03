package com.noljanolja.android.common.base

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

/**
 * Created by tuyen.dang on 5/11/2023.
 */

@Composable
internal fun BaseLifeCycle(
    onCreate: () -> Unit = {},
    onStart: () -> Unit = {},
    onResume: () -> Unit = {},
    onPause: () -> Unit = {},
    onStop: () -> Unit = {},
    onDestroy: () -> Unit = {},
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {
    DisposableEffect(lifecycleOwner) {
        // Create an observer that triggers our remembered callbacks
        // for sending analytics events
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> {
                    Log.d("TTT", "ON_CREATE: $lifecycleOwner")
                    onCreate()
                }
                Lifecycle.Event.ON_START ->{
                    Log.d("TTT", "ON_START: $lifecycleOwner")
                    onStart()
                }
                Lifecycle.Event.ON_RESUME -> {
                    Log.d("TTT", "ON_RESUME: $lifecycleOwner")
                    onResume()
                }
                Lifecycle.Event.ON_PAUSE -> {
                    Log.d("TTT", "ON_PAUSE: $lifecycleOwner")
                    onPause()
                }
                Lifecycle.Event.ON_STOP -> {
                    Log.d("TTT", "ON_STOP: $lifecycleOwner")
                    onStop()
                }
                Lifecycle.Event.ON_DESTROY -> {
                    Log.d("TTT", "ON_DESTROY: $lifecycleOwner")
                    onDestroy()
                }
                else -> {}
            }
        }

        // Add the observer to the lifecycle
        lifecycleOwner.lifecycle.addObserver(observer)

        // When the effect leaves the Composition, remove the observer
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}
