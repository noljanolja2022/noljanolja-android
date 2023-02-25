package com.noljanolja.android.common.navigation

import androidx.navigation.*
import java.net.URLEncoder

object NavigationDirections {
    object Root : NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options: NavOptions? = null
        override val destination: String = "root"
    }

    object Splash : NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options: NavOptions = navOptions {
            popUpTo(Root.destination) {
                inclusive = true
            }
            launchSingleTop = true
        }
        override val destination: String = "splash"
    }

    // AUTH

    object LoginOrSignup : NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options: NavOptions = navOptions {
            popUpTo(Root.destination) {
                inclusive = true
            }
            launchSingleTop = true
        }

        override val destination: String = "loginOrSignup"
    }

    object Signup : NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options: NavOptions = navOptions {
            launchSingleTop = true
        }
        override val destination: String = "signup"
    }

    object Forgot : NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options: NavOptions = navOptions {
            launchSingleTop = true
        }
        override val destination: String = "forget"
    }

    object TermsOfService : NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options: NavOptions = navOptions {
            launchSingleTop = true
        }
        override val destination: String = "termsOfService"
    }

    object CountryPicker : NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options: NavOptions = navOptions {
            launchSingleTop = true
        }
        override val destination: String = "country_picker"
    }

    data class AuthOTP(
        val phone: String = "",
    ) : NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf(
            navArgument("phone") {
                defaultValue = ""
                type = NavType.StringType
            },
        )
        override val options: NavOptions = navOptions {
            launchSingleTop = true
        }
        override val destination: String = "auth_otp?phone={phone}"
        override fun createDestination(): String {
            return "auth_otp?phone=${URLEncoder.encode(phone, Charsets.UTF_8.name())}"
        }
    }

    // Home

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

    object ChatItem : NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options = null
        override val destination: String = "home_chat_item"
    }

    object CelebrationItem : NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options = null
        override val destination: String = "home_celebration_item"
    }

    object PlayItem : NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options = null
        override val destination: String = "home_play_item"
    }

    object StoreItem : NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options = null
        override val destination: String = "home_store_item"
    }

    object UserItem : NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options = null
        override val destination: String = "home_user_item"
    }

    object MyInfo : NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options = null
        override val destination: String = "my_info"
    }

    object Setting : NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options = null
        override val destination: String = "setting"
    }

    object Back : NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options: NavOptions? = null
        override val destination: String = "back"
    }

    // Back
    data class FinishWithResults(
        val data: Map<String, Any>,
    ) : NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options: NavOptions? = null
        override val destination: String = "back"
    }
}
