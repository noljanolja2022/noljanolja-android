package com.noljanolja.android

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.noljanolja.android.features.auth.screen.LoginScreen
import com.noljanolja.android.features.home.screen.HomeScreen
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.android.common.navigation.NavigationManager
import com.noljanolja.android.common.auth.domain.model.User

@Composable
fun MainScreen(
    navigationManager: NavigationManager,
    user: User?
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
