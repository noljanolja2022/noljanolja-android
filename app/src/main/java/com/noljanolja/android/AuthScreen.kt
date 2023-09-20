package com.noljanolja.android

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.android.common.navigation.NavigationManager
import com.noljanolja.android.util.showToast
import com.noljanolja.core.CoreManager
import org.koin.androidx.compose.get

@Composable
fun AuthScreen(
    navigationManager: NavigationManager,
) {
    val context = LocalContext.current
    val coreManager: CoreManager = get()
    val navController = rememberNavController()
    LaunchedEffect(key1 = coreManager.getRemovedConversationEvent()) {
        coreManager.getRemovedConversationEvent().collect {
            context.showToast(
                context.getString(
                    R.string.chat_removed_conversation,
                    it.getDisplayTitle()
                )
            )
        }
    }
    LaunchedEffect(navigationManager.commands) {
        navigationManager.commands.collect { commands ->
            val destination = commands.createDestination()
            if (destination.isNotEmpty()) {
                when (commands) {
                    is NavigationDirections.Home -> {
                        context.startActivity(Intent(context, MainActivity::class.java))
                        (context as Activity).finish()
                    }

                    is NavigationDirections.PhoneSettings -> {
                        context.startActivity(
                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", context.packageName, null)
                            }
                        )
                    }

                    is NavigationDirections.Back -> {
                        navController.popBackStack()
                    }

                    is NavigationDirections.FinishWithResults -> {
                        navController.previousBackStackEntry?.savedStateHandle?.apply {
                            commands.data.forEach { (key, value) ->
                                this[key] = value
                            }
                        }
                        navController.popBackStack()
                    }

                    else -> navController.navigate(destination, commands.options)
                }
            }
        }
    }
    NavHost(
        navController = navController,
        route = NavigationDirections.Root.destination,
        startDestination = NavigationDirections.Splash.destination,
    ) {
//        addSplashGraph()
//        addAuthGraph()
//        addContactsGraph()
    }
}
