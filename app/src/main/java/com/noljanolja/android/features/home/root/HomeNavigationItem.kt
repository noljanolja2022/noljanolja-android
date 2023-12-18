package com.noljanolja.android.features.home.root

import androidx.annotation.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.*
import com.noljanolja.android.R
import com.noljanolja.android.common.navigation.*

sealed class HomeNavigationItem(
    val route: String,
    var icon: ImageVector,
    val selectedIcon: ImageVector,
    @StringRes val label: Int,
) {
    object ChatItem : HomeNavigationItem(
        NavigationDirections.ChatItem.destination,
        Icons.Filled.ChatBubble,
        Icons.Outlined.ChatBubble,
        R.string.home_chats,
    )

    object FriendsItem : HomeNavigationItem(
        NavigationDirections.FriendItem.destination,
        Icons.Outlined.Group,
        Icons.Outlined.Group,
        R.string.home_friends,
    )

    object WatchItem : HomeNavigationItem(
        NavigationDirections.CelebrationItem.destination,
        Icons.Filled.OndemandVideo,
        Icons.Outlined.OndemandVideo,
        R.string.home_watch,
    )

    object WalletItem : HomeNavigationItem(
        NavigationDirections.PlayItem.destination,
        Icons.Filled.AccountBalanceWallet,
        Icons.Outlined.AccountBalanceWallet,
        R.string.home_wallet,
    )

    object StoreItem : HomeNavigationItem(
        NavigationDirections.StoreItem.destination,
        Icons.Filled.Store,
        Icons.Outlined.Store,
        R.string.home_shop,
    )
//
//    object NewsItem : HomeNavigationItem(
//        NavigationDirections.UserItem.destination,
//        Icons.Filled.Event,
//        Icons.Outlined.Event,
//        R.string.home_news,
//    )
}
