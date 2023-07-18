package com.noljanolja.android.common.navigation

import android.net.Uri
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navOptions
import com.noljanolja.android.features.home.wallet.model.UiLoyaltyPoint
import com.noljanolja.core.utils.defaultJson
import kotlinx.serialization.encodeToString
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

    object SearchVideos : NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options = null
        override val destination: String = "search_videos"
    }

    object TransactionHistory : NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options = null
        override val destination: String = "transaction_history"
    }

    object MyRanking : NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options = null
        override val destination: String = "my_ranking"
    }

    object AddFriend : NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options = null
        override val destination: String = "add_friend"
    }

    object SearchFriend : NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options = null
        override val destination: String = "search_friend"
    }

    object SearchFriendResult : NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options = null
        override val destination: String = "search_friend_result"
    }

    data class Dashboard(
        val month: Int = 0,
        val year: Int = 0,
    ) : NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf(
            navArgument("month") {
                defaultValue = 0
                type = NavType.IntType
            },
            navArgument("year") {
                defaultValue = 0
                type = NavType.IntType
            },
        )
        override val options = null
        override val destination: String = "wallet_dashboard?month={month}&year={year}"
        override fun createDestination(): String {
            return "wallet_dashboard?month=$month&year=$year"
        }
    }

    object FAQ : NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options = null
        override val destination: String = "faq"
    }

    object Licenses : NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options = null
        override val destination: String = "licence"
    }

    object AboutUs : NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options = null
        override val destination: String = "about_us"
    }

    data class TransactionDetail(
        val transaction: UiLoyaltyPoint = UiLoyaltyPoint(),
    ) : NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf(
            navArgument("transaction") {
                defaultValue = UiLoyaltyPoint()
                type = serializableType<NavObject<UiLoyaltyPoint>>()
            },
        )
        override val options = null
        override val destination: String = "transaction_detail?transaction={transaction}"
        override fun createDestination(): String {
            return "transaction_detail?transaction=${parseSerializableArgs(transaction)}"
        }
    }

    object ChatSettings : NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options = null
        override val destination: String = "chat_settings"
    }

    object ScanQrCode : NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options = null
        override val destination: String = "scan_qr_code"
    }

    object SearchProduct : NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options = null
        override val destination: String = "search_product"
    }

    data class GiftDetail(val giftId: Long = 0, val code: String = "") : NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf(
            navArgument("giftId") {
                defaultValue = 0
                type = NavType.LongType
            },
            navArgument("code") {
                defaultValue = ""
                type = NavType.StringType
            },
        )
        override val options = null
        override val destination: String = "gift_detail?giftId={giftId}&code={code}"
        override fun createDestination() = "gift_detail?giftId=$giftId&code=$code"
    }

    object Coupons : NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options = null
        override val destination: String = "coupons"
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

    data class SelectShareMessage(
        val selectMessageId: Long,
        val fromConversationId: Long,
    ) : NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf(
            navArgument("selectMessageId") {
                defaultValue = 0
                type = NavType.LongType
            },
            navArgument("fromConversationId") {
                defaultValue = 0
                type = NavType.LongType
            },
        )
        override val options: NavOptions? = null
        override val destination: String =
            "select_share_message?selectMessageId={selectMessageId}&fromConversationId={fromConversationId}"

        override fun createDestination() =
            "select_share_message?selectMessageId=$selectMessageId&fromConversationId=$fromConversationId"
    }
}

private inline fun <reified T> parseSerializableArgs(data: T): String {
    return Uri.encode(defaultJson().encodeToString(NavObject(data)))
}
