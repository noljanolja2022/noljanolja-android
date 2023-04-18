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

    object TermsOfService : NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options: NavOptions = navOptions {
            popUpTo(Root.destination) {
                inclusive = true
            }
            launchSingleTop = true
        }
        override val destination: String = "terms_of_service"
    }

    // AUTH

    object Auth : NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options: NavOptions = navOptions {
            launchSingleTop = true
            popUpTo(Root.destination) {
                inclusive = true
            }
        }

        override val destination: String = "auth"
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

    object UpdateProfile : NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options: NavOptions = navOptions {
            popUpTo(Root.destination) {
                inclusive = true
            }
            launchSingleTop = true
        }
        override val destination: String = "update_profile"
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

    // Chat
    data class Chat(
        val conversationId: Long = 0,
        val userIds: String = "[]",
        val title: String = "",
    ) : NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf(
            navArgument("conversationId") {
                defaultValue = 0
                type = NavType.LongType
            },
            navArgument("userIds") {
                defaultValue = ""
                type = NavType.StringType
            },
            navArgument("title") {
                defaultValue = ""
                type = NavType.StringType
            },
        )
        override val options: NavOptions = navOptions {
            launchSingleTop = true
        }
        override val destination: String =
            "chat?conversationId={conversationId}&userIds={userIds}&title={title}"

        override fun createDestination(): String {
            return "chat?conversationId=$conversationId&userIds=$userIds&title=$title"
        }
    }

    data class ChatOptions(
        val conversationId: Long = 0L,
    ) : NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf(
            navArgument("conversationId") {
                defaultValue = 0
                type = NavType.LongType
            },
        )
        override val options: NavOptions = navOptions {
            launchSingleTop = true
        }
        override val destination: String =
            "chat_options?conversationId={conversationId}"

        override fun createDestination(): String {
            return "chat_options?conversationId=$conversationId"
        }
    }

    data class EditChatTitle(
        val conversationId: Long = 0L,
    ) : NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf(
            navArgument("conversationId") {
                defaultValue = 0
                type = NavType.LongType
            },
        )
        override val options: NavOptions = navOptions {
            launchSingleTop = true
        }
        override val destination: String =
            "edit_chat_title?conversationId={conversationId}"

        override fun createDestination(): String {
            return "edit_chat_title?conversationId=$conversationId"
        }
    }

    data class SelectContact(
        val type: String = "",
        val conversationId: Long = 0L,
    ) : NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf(
            navArgument("type") {
                defaultValue = "SINGLE"
                type = NavType.StringType
            },
            navArgument("conversationId") {
                defaultValue = 0
                type = NavType.LongType
            },
        )
        override val options: NavOptions = navOptions {
            launchSingleTop = true
        }
        override val destination: String =
            "select_contact?type={type}&conversationId={conversationId}"

        override fun createDestination(): String {
            return "select_contact?type=$type&conversationId=$conversationId"
        }
    }

    data class PlayScreen(val videoId: String = "") : NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf(
            navArgument("videoId") {
                defaultValue = ""
                type = NavType.StringType
            },
        )
        override val options = null
        override val destination: String = "play_screen?videoId={videoId}"
        override fun createDestination(): String {
            return "play_screen?videoId=$videoId"
        }
    }

    // Back
    data class FinishWithResults(
        val data: Map<String, Any>,
    ) : NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options: NavOptions? = null
        override val destination: String = "back"
    }

    object PhoneSettings : NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options: NavOptions? = null
        override val destination: String = "phone_settings"
    }
}
