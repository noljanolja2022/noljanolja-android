package com.noljanolja.android.features.home.root

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Celebration
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Person
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
        Icons.Outlined.Chat,
        Icons.Outlined.Chat,
        R.string.home_chats,
    )

    object CelebrationItem : HomeNavigationItem(
        NavigationDirections.CelebrationItem.destination,
        Icons.Outlined.Celebration,
        Icons.Outlined.Celebration,
        R.string.home_events,
    )

    object PlayItem : HomeNavigationItem(
        NavigationDirections.PlayItem.destination,
        Icons.Default.PlayCircle,
        Icons.Default.PlayCircle,
        R.string.home_contents,
    )

    object StoreItem : HomeNavigationItem(
        NavigationDirections.StoreItem.destination,
        Icons.Default.Store,
        Icons.Default.Store,
        R.string.home_shop,
    )

    object UserItem : HomeNavigationItem(
        NavigationDirections.UserItem.destination,
        Icons.Outlined.Person,
        Icons.Outlined.Person,
        R.string.home_person,
    )
}
