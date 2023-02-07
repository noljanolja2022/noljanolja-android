package com.noljanolja.android.features.home.screen

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.noljanolja.android.R
import com.noljanolja.android.common.navigation.NavigationDirections

sealed class HomeNavigationItem(
    val route: String,
    @DrawableRes val icon: Int,
    @DrawableRes val selectedIcon: Int,
    @StringRes val label: Int,
) {
    object HomeItem1 : HomeNavigationItem(
        NavigationDirections.HomeItem1.destination,
        R.drawable.ic_launcher_background,
        R.drawable.ic_launcher_foreground,
        R.string.home_item_1,
    )

    object HomeItem2 : HomeNavigationItem(
        NavigationDirections.HomeItem2.destination,
        R.drawable.ic_launcher_background,
        R.drawable.ic_launcher_foreground,
        R.string.home_item_1,
    )

    object HomeItem3 : HomeNavigationItem(
        NavigationDirections.HomeItem3.destination,
        R.drawable.ic_launcher_background,
        R.drawable.ic_launcher_foreground,
        R.string.home_item_1,
    )

    object HomeItem4 : HomeNavigationItem(
        NavigationDirections.HomeItem4.destination,
        R.drawable.ic_launcher_background,
        R.drawable.ic_launcher_foreground,
        R.string.home_item_1,
    )
}