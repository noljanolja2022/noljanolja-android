package com.noljanolja.android.features.home.component

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.noljanolja.android.R
import com.noljanolja.android.features.home.root.screen.HomeNavigationItem
import com.noljanolja.android.features.home.utils.click
import com.noljanolja.android.features.home.utils.isNavItemSelect

@Composable
fun HomeFloatingActionButton(navController: NavHostController) {
    val item = HomeNavigationItem.WalletItem
    val isSelected = item.isNavItemSelect(navController = navController)
    FloatingActionButton(
        shape = CircleShape,
        backgroundColor = if (isSelected) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
        contentColor = Color.White,
        modifier = Modifier
            .size(68.dp),
        onClick = {
            HomeNavigationItem.WalletItem.click(navController)
        },
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_wallet),
            contentDescription = null,
            modifier = Modifier.size(36.dp),
        )
    }
}
