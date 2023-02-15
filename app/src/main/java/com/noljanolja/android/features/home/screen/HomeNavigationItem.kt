package com.noljanolja.android.features.home.screen

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.noljanolja.android.R
import com.noljanolja.android.common.navigation.NavigationDirections

sealed class HomeNavigationItem(
    val route: String,
    @DrawableRes val icon: Int,
    @DrawableRes val selectedIcon: Int,
    @StringRes val label: Int?,
) {
    object MenuItem : HomeNavigationItem(
        NavigationDirections.MenuItem.destination,
        R.drawable.ic_menu,
        R.drawable.ic_menu,
        null,
    )

    object HomeItem : HomeNavigationItem(
        NavigationDirections.HomeItem.destination,
        R.drawable.ic_house,
        R.drawable.ic_house,
        null,
    )

    object WalletItem : HomeNavigationItem(
        NavigationDirections.WalletItem.destination,
        R.drawable.ic_wallet,
        R.drawable.ic_wallet,
        null,
    )

    object ShopItem : HomeNavigationItem(
        NavigationDirections.StoreItem.destination,
        R.drawable.ic_store,
        R.drawable.ic_store,
        null,
    )

    object UserItem : HomeNavigationItem(
        NavigationDirections.UserItem.destination,
        R.drawable.ic_user,
        R.drawable.ic_user,
        null,
    )
}