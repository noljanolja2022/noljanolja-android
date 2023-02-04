package com.noljanolja.android

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseUser
import com.noljanolja.android.ui.screen.auth.LoginScreen
import com.noljanolja.android.ui.screen.home.HomeScreen
import com.noljanolja.android.ui.screen.navigation.NavigationDirections
import com.noljanolja.android.ui.screen.navigation.NavigationManager

@Composable
fun MainScreen(
    navigationManager: NavigationManager,
    user: FirebaseUser?
) {
    val navController = rememberNavController()
    LaunchedEffect(navigationManager.commands) {
        navigationManager.commands.collect { commands ->
            val destination = commands.createDestination()
            if (destination.isNotEmpty()) {
                when (commands) {
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
        startDestination = user?.let { NavigationDirections.Home.destination }
            ?: NavigationDirections.Login.destination
    ) {
        composable(NavigationDirections.Home.destination) { backStack ->
            HomeScreen()
        }
        composable(NavigationDirections.Login.destination) { backStack ->
            LoginScreen()
        }
        composable(NavigationDirections.HomeItem4.destination) {
            Text("NewScreen")
        }
    }
}
