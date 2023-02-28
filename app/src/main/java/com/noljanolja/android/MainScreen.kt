package com.noljanolja.android

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.android.common.navigation.NavigationManager
import com.noljanolja.android.features.auth.countries.CountriesScreen
import com.noljanolja.android.features.auth.login_or_signup.LoginOrSignupScreen
import com.noljanolja.android.features.auth.otp.OTPScreen
import com.noljanolja.android.features.auth.terms_of_service.TermsOfServiceScreen
import com.noljanolja.android.features.auth.updateprofile.UpdateProfileScreen
import com.noljanolja.android.features.home.info.MyInfoScreen
import com.noljanolja.android.features.home.root.HomeScreen
import com.noljanolja.android.features.setting.SettingScreen
import com.noljanolja.android.features.splash.SplashScreen

@Composable
fun MainScreen(
    navigationManager: NavigationManager,
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
        startDestination = NavigationDirections.Splash.destination,
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
    composable(NavigationDirections.TermsOfService.destination) {
        TermsOfServiceScreen()
    }
}

private fun NavGraphBuilder.addHomeGraph() {
    composable(NavigationDirections.Home.destination) { backStack ->
        HomeScreen()
    }
    composable(NavigationDirections.MyInfo.destination) {
        MyInfoScreen()
    }
    composable(NavigationDirections.Setting.destination) { backStack ->
        SettingScreen()
    }
}

private fun NavGraphBuilder.addAuthGraph() {
    composable(NavigationDirections.Auth.destination) { backStack ->
        LoginOrSignupScreen(backStack.savedStateHandle)
    }
    composable(NavigationDirections.CountryPicker.destination) {
        CountriesScreen()
    }
    with(NavigationDirections.AuthOTP()) {
        composable(
            destination,
            arguments,
        ) { backStack ->
            with(backStack.arguments) {
                val phone = this?.getString("phone") ?: ""
                OTPScreen(phone = phone)
            }
        }
    }
    composable(NavigationDirections.UpdateProfile.destination) {
        UpdateProfileScreen()
    }
//    composable(NavigationDirections.Forgot.destination) {
//        ForgotScreen()
//    }
//    composable(NavigationDirections.TermsOfService.destination) {
//        TermsOfServiceScreen()
//    }
}
