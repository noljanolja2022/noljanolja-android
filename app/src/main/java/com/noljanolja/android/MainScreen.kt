package com.noljanolja.android

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.noljanolja.android.ui.screen.home.HomeScreen
import com.noljanolja.android.ui.screen.navigation.NavigationDirections

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        route = NavigationDirections.Root.destination,
        startDestination = NavigationDirections.Home.destination,
    ) {
        composable(NavigationDirections.Home.destination) { backStack ->
            HomeScreen()
        }
    }
}