package com.noljanolja.android

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.noljanolja.android.ui.screen.home.HomeScreen
import com.noljanolja.android.ui.screen.navigation.NavigationDirections
import com.noljanolja.android.ui.screen.navigation.NavigationManager

@Composable
fun MainScreen(
    navigationManager: NavigationManager
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
                                this.set(key, value)
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
        startDestination = NavigationDirections.Home.destination
    ) {
        composable(NavigationDirections.Home.destination) { backStack ->
            HomeScreen()
        }
        composable(NavigationDirections.HomeItem4.destination) {
            Text("NewScreen")
        }
    }
}
