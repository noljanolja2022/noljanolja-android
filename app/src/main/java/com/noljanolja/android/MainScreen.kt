package com.noljanolja.android

import android.app.*
import android.content.*
import android.net.*
import android.provider.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.*
import androidx.lifecycle.*
import androidx.navigation.*
import androidx.navigation.compose.*
import com.noljanolja.android.common.navigation.*
import com.noljanolja.android.extensions.*
import com.noljanolja.android.features.addfriend.*
import com.noljanolja.android.features.addreferral.*
import com.noljanolja.android.features.auth.countries.*
import com.noljanolja.android.features.auth.login.*
import com.noljanolja.android.features.auth.otp.*
import com.noljanolja.android.features.auth.termdetail.*
import com.noljanolja.android.features.auth.terms_of_service.*
import com.noljanolja.android.features.auth.updateprofile.*
import com.noljanolja.android.features.chatsettings.*
import com.noljanolja.android.features.conversationmedia.*
import com.noljanolja.android.features.edit_chat_title.*
import com.noljanolja.android.features.home.*
import com.noljanolja.android.features.home.chat.*
import com.noljanolja.android.features.home.chat_options.*
import com.noljanolja.android.features.home.contacts.*
import com.noljanolja.android.features.home.friendoption.*
import com.noljanolja.android.features.home.info.*
import com.noljanolja.android.features.home.play.playscreen.*
import com.noljanolja.android.features.home.play.search.*
import com.noljanolja.android.features.home.play.uncompleted.*
import com.noljanolja.android.features.home.root.*
import com.noljanolja.android.features.home.sendpoint.SendPointScreen
import com.noljanolja.android.features.home.wallet.checkin.*
import com.noljanolja.android.features.home.wallet.dashboard.*
import com.noljanolja.android.features.home.wallet.detail.*
import com.noljanolja.android.features.home.wallet.exchange.*
import com.noljanolja.android.features.home.wallet.model.*
import com.noljanolja.android.features.home.wallet.myranking.*
import com.noljanolja.android.features.home.wallet.transaction.*
import com.noljanolja.android.features.images.*
import com.noljanolja.android.features.qrcode.*
import com.noljanolja.android.features.referral.*
import com.noljanolja.android.features.setting.*
import com.noljanolja.android.features.setting.more.*
import com.noljanolja.android.features.sharemessage.*
import com.noljanolja.android.features.shop.coupons.*
import com.noljanolja.android.features.shop.giftdetail.*
import com.noljanolja.android.features.shop.productbycategory.*
import com.noljanolja.android.features.shop.search.*
import com.noljanolja.android.features.splash.*
import com.noljanolja.android.util.*
import com.noljanolja.core.*
import com.noljanolja.core.conversation.domain.model.*
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
                            MyApplication.clearAllPipActivities()
                            startActivity(
                                PlayVideoActivity.createIntent(
                                    this,
                                    commands.videoId,
                                    isInPictureInPictureMode = commands.isInPipMode
                                )
                            )
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
    with(NavigationDirections.SendPointScreen()) {
        composable(
            destination,
            arguments
        ) {
            val friendId = it.arguments?.getString("friendId")
            val friendName = it.arguments?.getString("friendName")
            val friendAvatar = it.arguments?.getString("friendAvatar")
            val isRequestPoint = it.arguments?.getBoolean("isRequestPoint")
            SendPointScreen(
                friendId = friendId.convertToString(),
                friendName = friendName.convertToString(),
                friendAvatar = friendAvatar.convertToString(),
                isRequestPoint = isRequestPoint ?: true
            )
        }
    }
    with(NavigationDirections.FriendOption()) {
        composable(
            destination,
            arguments
        ) {
            val friendId = it.arguments?.getString("friendId")
            val friendName = it.arguments?.getString("friendName")
            val friendAvatar = it.arguments?.getString("friendAvatar")
            FriendOptionScreen(
                friendId = friendId.convertToString(),
                friendName = friendName.convertToString(),
                friendAvatar = friendAvatar.convertToString()
            )
        }
    }
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
    with(NavigationDirections.ExchangeCoin) {
        composable(destination, arguments) {
            ExchangePointScreen()
        }
    }
}

private fun NavGraphBuilder.addShopGraph() {
    with(NavigationDirections.SearchProduct) {
        composable(destination, arguments) {
            SearchProductScreen()
        }
    }
    with(NavigationDirections.ProductByCategory()) {
        composable(destination, arguments) {
            val brandId = it.arguments?.getString("brandId")
            val categoryId = it.arguments?.getString("categoryId")
            val categoryName = it.arguments?.getString("categoryName")
            ProductByCategoryScreen(
                brandId = brandId.convertToString(),
                categoryId = categoryId.convertToString(),
                categoryName = categoryName.convertToString()
            )
        }
    }
    with(NavigationDirections.GiftDetail()) {
        composable(
            destination,
            arguments
        ) {
            val giftId = (it.arguments?.getString("giftId")).orEmpty()
            val code = (it.arguments?.getString("code").orEmpty())
            GiftDetailScreen(giftId, code)
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
    with(NavigationDirections.TermDetail()) {
        composable(destination, arguments) {
            val termIndex = it.arguments?.getInt("termIndex") ?: 1
            TermDetailScreen(termIndex)
        }
    }
}

private fun NavGraphBuilder.addAuthGraph() {
    composable(NavigationDirections.Auth.destination) { backStack ->
        LoginScreen(backStack.savedStateHandle)
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
    composable(NavigationDirections.UpdateProfile.destination) { backStack ->
        UpdateProfileScreen(backStack.savedStateHandle)
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