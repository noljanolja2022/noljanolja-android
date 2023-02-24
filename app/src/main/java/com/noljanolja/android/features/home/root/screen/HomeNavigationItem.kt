package com.noljanolja.android.features.home.root.screen

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.noljanolja.android.common.navigation.NavigationDirections

sealed class HomeNavigationItem(
    val route: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
    @StringRes val label: Int?,
) {
    object ChatItem : HomeNavigationItem(
        NavigationDirections.MenuItem.destination,
        Icons.Default.Chat,
        Icons.Default.Chat,
        null,
    )

    object CelebrationItem : HomeNavigationItem(
        NavigationDirections.HomeItem.destination,
        Icons.Default.Celebration,
        Icons.Default.Celebration,
        null,
    )

    object PlayItem : HomeNavigationItem(
        NavigationDirections.WalletItem.destination,
        Icons.Default.PlayCircle,
        Icons.Default.PlayCircle,
        null,
    )

    object ShopItem : HomeNavigationItem(
        NavigationDirections.StoreItem.destination,
        Icons.Default.Store,
        Icons.Default.Store,
        null,
    )

    object UserItem : HomeNavigationItem(
        NavigationDirections.UserItem.destination,
        Icons.Default.Person,
        Icons.Default.Person,
        null,
    )
}
