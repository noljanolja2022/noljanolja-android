package com.noljanolja.android.features.home.root

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.ChatBubble
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.OndemandVideo
import androidx.compose.material.icons.outlined.Store
import androidx.compose.ui.graphics.vector.ImageVector
import com.noljanolja.android.R
import com.noljanolja.android.common.navigation.NavigationDirections

sealed class HomeNavigationItem(
    val route: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
    @StringRes val label: Int,
) {
    object ChatItem : HomeNavigationItem(
        NavigationDirections.ChatItem.destination,
        Icons.Filled.ChatBubble,
        Icons.Outlined.ChatBubble,
        R.string.home_chats,
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
