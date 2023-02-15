package com.noljanolja.android

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.android.common.navigation.NavigationManager
import com.noljanolja.android.features.auth.TermsOfServiceScreen
import com.noljanolja.android.features.auth.forget.screen.ForgotScreen
import com.noljanolja.android.features.auth.login_or_signup.screen.LoginOrSignupScreen
import com.noljanolja.android.features.home.root.screen.HomeScreen
import com.noljanolja.android.features.splash.screen.SplashScreen

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
        startDestination = NavigationDirections.Splash.destination
    ) {
        addSplashGraph()
        addHomeGraph()
        addAuthGraph()
    }
}

private fun NavGraphBuilder.addSplashGraph() {
    composable(NavigationDirections.Splash.destination) {
        SplashScreen()
    }
}

private fun NavGraphBuilder.addHomeGraph() {
    composable(NavigationDirections.Home.destination) { backStack ->
        HomeScreen()
    }
}

private fun NavGraphBuilder.addAuthGraph() {
    composable(NavigationDirections.LoginOrSignup.destination) { backStack ->
        LoginOrSignupScreen()
    }
    composable(NavigationDirections.Forgot.destination) {
        ForgotScreen()
    }
    composable(NavigationDirections.TermsOfService.destination) {
        TermsOfServiceScreen()
    }
}
