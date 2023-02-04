package com.noljanolja.android.ui.screen.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavOptions
import androidx.navigation.navOptions

object NavigationDirections {
    object Root : NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options: NavOptions? = null
        override val destination: String = "root"
    }

    object Login : NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options: NavOptions = navOptions {
            popUpTo(Root.destination) {
                inclusive = true
            }
            launchSingleTop = true
        }
        override val destination: String = "login"
    }

    object Home : NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options: NavOptions = navOptions {
            popUpTo(Root.destination) {
                inclusive = true
            }
            launchSingleTop = true
        }
        override val destination: String = "home"
    }

    object HomeItem1 : NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options = null
        override val destination: String = "Home1"
    }

    object HomeItem2 : NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options = null
        override val destination: String = "Home2"
    }

    object HomeItem3 : NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options = null
        override val destination: String = "Home3"
    }

    object HomeItem4 : NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options = null
        override val destination: String = "Home4"
    }

    object Back : NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options: NavOptions? = null
        override val destination: String = "back"
    }

    // Back
    data class FinishWithResults(
        val data: Map<String, Any>
    ) : NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options: NavOptions? = null
        override val destination: String = "back"
    }
}
