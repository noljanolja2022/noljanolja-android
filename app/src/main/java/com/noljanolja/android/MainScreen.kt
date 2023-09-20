package com.noljanolja.android

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.noljanolja.android.common.navigation.NavObject
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.android.common.navigation.NavigationManager
import com.noljanolja.android.features.addfriend.AddFriendViewModel
import com.noljanolja.android.features.addfriend.SearchFriendResultScreen
import com.noljanolja.android.features.addfriend.SearchFriendScreen
import com.noljanolja.android.features.addreferral.AddReferralScreen
import com.noljanolja.android.features.auth.countries.CountriesScreen
import com.noljanolja.android.features.auth.login_or_signup.LoginOrSignupScreen
import com.noljanolja.android.features.auth.otp.OTPScreen
import com.noljanolja.android.features.auth.terms_of_service.TermsOfServiceScreen
import com.noljanolja.android.features.auth.updateprofile.UpdateProfileScreen
import com.noljanolja.android.features.chatsettings.ChatSettingsScreen
import com.noljanolja.android.features.conversationmedia.ConversationMediaScreen
import com.noljanolja.android.features.edit_chat_title.EditChatTitleScreen
import com.noljanolja.android.features.home.CheckinViewModel
import com.noljanolja.android.features.home.chat.ChatScreen
import com.noljanolja.android.features.home.chat_options.ChatOptionsScreen
import com.noljanolja.android.features.home.contacts.ContactsScreen
import com.noljanolja.android.features.home.info.MyInfoScreen
import com.noljanolja.android.features.home.play.playscreen.PlayVideoActivity
import com.noljanolja.android.features.home.play.search.SearchVideosScreen
import com.noljanolja.android.features.home.play.uncompleted.UncompletedVideosScreen
import com.noljanolja.android.features.home.root.HomeScreen
import com.noljanolja.android.features.home.wallet.checkin.CheckinScreen
import com.noljanolja.android.features.home.wallet.dashboard.WalletDashboardScreen
import com.noljanolja.android.features.home.wallet.detail.TransactionDetailScreen
import com.noljanolja.android.features.home.wallet.model.UiLoyaltyPoint
import com.noljanolja.android.features.home.wallet.myranking.MyRankingScreen
import com.noljanolja.android.features.home.wallet.transaction.TransactionsHistoryScreen
import com.noljanolja.android.features.images.ViewImagesScreen
import com.noljanolja.android.features.qrcode.ScanQrCodeScreen
import com.noljanolja.android.features.referral.ReferralScreen
import com.noljanolja.android.features.setting.SettingScreen
import com.noljanolja.android.features.setting.more.AboutUsScreen
import com.noljanolja.android.features.setting.more.FAQScreen
import com.noljanolja.android.features.setting.more.LicenseScreen
import com.noljanolja.android.features.sharemessage.SelectShareMessageScreen
import com.noljanolja.android.features.shop.coupons.CouponsScreen
import com.noljanolja.android.features.shop.giftdetail.GiftDetailScreen
import com.noljanolja.android.features.shop.search.SearchProductScreen
import com.noljanolja.android.features.splash.SplashScreen
import com.noljanolja.android.util.orZero
import com.noljanolja.android.util.showToast
import com.noljanolja.android.util.toNavList
import com.noljanolja.core.CoreManager
import com.noljanolja.core.conversation.domain.model.ConversationType
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel

@Composable
fun MainScreen(
    navigationManager: NavigationManager,
) {
    val context = LocalContext.current
    val coreManager: CoreManager = get()
    val navController = rememberNavController()
    LaunchedEffect(key1 = coreManager.getRemovedConversationEvent()) {
        coreManager.getRemovedConversationEvent().collect {
            context.showToast(
                context.getString(
                    R.string.chat_removed_conversation,
                    it.getDisplayTitle()
                )
            )
        }
    }
    LaunchedEffect(navigationManager.commands) {
        navigationManager.commands.collect { commands ->
            val destination = commands.createDestination()
            if (destination.isNotEmpty()) {
                when (commands) {
                    is NavigationDirections.PlayScreen -> {
                        with(context as Activity) {
                            MyApplication.backStackActivities.apply {
                                forEach { it.finish() }
                                clear()
                            }
                            startActivity(PlayVideoActivity.createIntent(this, commands.videoId))
                        }
                    }

                    is NavigationDirections.PhoneSettings -> {
                        context.startActivity(
                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", context.packageName, null)
                            }
                        )
                    }

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
        addAuthGraph()
        addHomeGraph(navController)
        addContactsGraph()
        addChatGraph()
        addVideoGraph()
        addWalletGraph(navController)
        addAddFriendGraph(navController)
        addShopGraph()
        addViewImagesGraph()
    }
}

private fun NavGraphBuilder.addHomeGraph(
    navController: NavHostController,
) {
    composable(NavigationDirections.Home.destination) { backStack ->
        val checkinViewModel = backStack.sharedViewModel<CheckinViewModel>(
            navController = navController
        )
        HomeScreen(
            checkinViewModel = checkinViewModel,
        )
    }
    composable(NavigationDirections.MyInfo.destination) {
        MyInfoScreen()
    }
    composable(NavigationDirections.Setting.destination) { backStack ->
        SettingScreen()
    }
}

private fun NavGraphBuilder.addContactsGraph() {
    val direction = NavigationDirections.SelectContact()
    composable(
        direction.destination,
        direction.arguments
    ) { backStack ->
        val type = backStack.arguments?.getString("type") ?: "SINGLE"
        val conversationId = backStack.arguments?.getLong("conversationId") ?: 0L
        ContactsScreen(ConversationType.valueOf(type), conversationId)
    }
}

private fun NavGraphBuilder.addChatGraph() {
    val chatDirection = NavigationDirections.Chat()
    composable(
        chatDirection.destination,
        chatDirection.arguments,
    ) { backStack ->
        with(backStack.arguments) {
            val conversationId = (this?.getLong("conversationId") ?: 0)
            val userIds =
                (this?.getString("userIds"))?.split(",").orEmpty()
            val title = (this?.getString("title").orEmpty())
            ChatScreen(
                savedStateHandle = backStack.savedStateHandle,
                conversationId = conversationId,
                userIds = userIds,
                title = title
            )
        }
    }
    with(NavigationDirections.ChatOptions()) {
        composable(
            destination,
            arguments
        ) {
            val conversationId = (it.arguments?.getLong("conversationId") ?: 0)
            ChatOptionsScreen(
                conversationId = conversationId
            )
        }
    }
    with(NavigationDirections.EditChatTitle()) {
        composable(
            destination,
            arguments
        ) {
            val conversationId = (it.arguments?.getLong("conversationId") ?: 0)
            EditChatTitleScreen(conversationId = conversationId)
        }
    }
    with(NavigationDirections.ChatSettings) {
        composable(destination, arguments) {
            ChatSettingsScreen()
        }
    }
    with(NavigationDirections.ScanQrCode) {
        composable(
            destination,
            arguments
        ) {
            ScanQrCodeScreen()
        }
    }
    with(NavigationDirections.SelectShareMessage(selectMessageId = 0L, fromConversationId = 0L)) {
        composable(
            destination,
            arguments
        ) {
            val selectMessageId = (it.arguments?.getLong("selectMessageId") ?: 0)
            val fromConversationId = (it.arguments?.getLong("fromConversationId") ?: 0)
            SelectShareMessageScreen(selectMessageId, fromConversationId)
        }
    }
    with(NavigationDirections.ConversationMedia()) {
        composable(destination, arguments) {
            val conversationId = (it.arguments?.getLong("conversationId") ?: 0)
            ConversationMediaScreen(conversationId = conversationId)
        }
    }
}

private fun NavGraphBuilder.addVideoGraph() {
//    with(NavigationDirections.PlayScreen()) {
//        composable(destination, arguments) {
//            val videoId = (it.arguments?.getString("videoId").orEmpty())
//            VideoDetailScreen(videoId)
//        }
//    }
    with(NavigationDirections.SearchVideos) {
        composable(destination, arguments) {
            SearchVideosScreen()
        }
    }
    with(NavigationDirections.UncompletedVideos) {
        composable(destination, arguments) {
            UncompletedVideosScreen()
        }
    }
}

private fun NavGraphBuilder.addWalletGraph(navController: NavHostController) {
    with(NavigationDirections.TransactionHistory) {
        composable(destination, arguments) {
            TransactionsHistoryScreen()
        }
    }
    with(NavigationDirections.MyRanking) {
        composable(destination, arguments) {
            MyRankingScreen()
        }
    }
    with(NavigationDirections.Dashboard()) {
        composable(destination, arguments) {
            val month = it.arguments?.getInt("month").orZero()
            val year = it.arguments?.getInt("year").orZero()
            WalletDashboardScreen(month = month, year = year)
        }
    }
    with(NavigationDirections.TransactionDetail()) {
        composable(destination, arguments) {
            val navObject =
                it.arguments?.getSerializable("transaction") as? NavObject<UiLoyaltyPoint>
            val transaction = navObject?.data ?: UiLoyaltyPoint()
            TransactionDetailScreen(loyaltyPoint = transaction)
        }
    }
    with(NavigationDirections.FAQ) {
        composable(destination, arguments) {
            FAQScreen()
        }
    }
    with(NavigationDirections.Licenses) {
        composable(destination, arguments) {
            LicenseScreen()
        }
    }
    with(NavigationDirections.AboutUs) {
        composable(destination, arguments) {
            AboutUsScreen()
        }
    }
    with(NavigationDirections.Checkin) {
        composable(destination, arguments) { backStack ->
            val checkinViewModel = backStack.sharedViewModel<CheckinViewModel>(
                navController = navController
            )
            CheckinScreen(viewModel = checkinViewModel)
        }
    }
    with(NavigationDirections.Referral) {
        composable(
            destination,
            arguments
        ) { backStack ->
            ReferralScreen()
        }
    }
}

private fun NavGraphBuilder.addShopGraph() {
    with(NavigationDirections.SearchProduct) {
        composable(destination, arguments) {
            SearchProductScreen()
        }
    }
    with(NavigationDirections.GiftDetail()) {
        composable(
            destination,
            arguments
        ) {
            val giftId = (it.arguments?.getLong("giftId"))
            val code = (it.arguments?.getString("code").orEmpty())
            GiftDetailScreen(giftId.orZero(), code)
        }
    }
    with(NavigationDirections.Coupons) {
        composable(destination, arguments) {
            CouponsScreen()
        }
    }
}

private fun NavGraphBuilder.addViewImagesGraph() {
    with(NavigationDirections.ViewImages()) {
        composable(destination, arguments) {
            val images = it.arguments?.getString("images").orEmpty()
            ViewImagesScreen(images = images.toNavList())
        }
    }
}

private fun NavGraphBuilder.addAddFriendGraph(navController: NavHostController) {
    navigation(
        route = NavigationDirections.AddFriend.destination,
        startDestination = NavigationDirections.SearchFriend.destination,
    ) {
        with(NavigationDirections.SearchFriend) {
            composable(destination, arguments) { entry ->
                val addFriendViewModel = entry.sharedViewModel<AddFriendViewModel>(
                    navController = navController
                )
                SearchFriendScreen(
                    addFriendViewModel = addFriendViewModel,
                    savedStateHandle = entry.savedStateHandle
                )
            }
        }
        with(NavigationDirections.SearchFriendResult) {
            composable(destination, arguments) { entry ->
                val addFriendViewModel = entry.sharedViewModel<AddFriendViewModel>(
                    navController = navController
                )
                SearchFriendResultScreen(addFriendViewModel = addFriendViewModel)
            }
        }
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
    composable(NavigationDirections.AddReferral.destination) {
        AddReferralScreen()
    }
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(navController: NavController): T {
    val navGraphRoute = destination.parent?.route ?: return getViewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return getViewModel(viewModelStoreOwner = parentEntry)
}