package com.noljanolja.android.features.home.root

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.noljanolja.android.features.home.menu.MenuScreen
import com.noljanolja.android.features.home.mypage.MyPageScreen
import com.noljanolja.android.features.home.utils.click
import com.noljanolja.android.features.home.utils.isNavItemSelect
import com.noljanolja.android.ui.composable.FullSizeUnderConstruction
import com.noljanolja.android.util.getErrorMessage
import com.noljanolja.android.util.showToast
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    LaunchedEffect(key1 = viewModel.errorFlow) {
        launch {
            viewModel.errorFlow.collect {
                context.showToast(context.getErrorMessage(it))
            }
        }
    }
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            HomeBottomBar(
                navController,
            )
        },
    ) { contentPadding ->
        NavHost(
            navController = navController,
            startDestination = HomeNavigationItem.CelebrationItem.route,
            modifier = Modifier.padding(contentPadding),
        ) {
            addNavigationGraph()
        }
    }
}

private fun NavGraphBuilder.addNavigationGraph() {
    composable(HomeNavigationItem.ChatItem.route) {
        MenuScreen()
    }
    composable(HomeNavigationItem.CelebrationItem.route) {
        FullSizeUnderConstruction()
    }
    composable(HomeNavigationItem.PlayItem.route) {
        FullSizeUnderConstruction()
    }
    composable(HomeNavigationItem.StoreItem.route) {
        FullSizeUnderConstruction()
    }
    composable(HomeNavigationItem.UserItem.route) {
        MyPageScreen()
    }
}

@Composable
fun HomeBottomBar(
    navController: NavHostController,
) {
    val items = listOf(
        HomeNavigationItem.ChatItem,
        HomeNavigationItem.CelebrationItem,
        HomeNavigationItem.PlayItem,
        HomeNavigationItem.StoreItem,
        HomeNavigationItem.UserItem,
    )
    NavigationBar(
        tonalElevation = 0.dp,
        containerColor = Color.White,
    ) {
        items.forEach { item ->
            val isSelected = item.isNavItemSelect(navController = navController)
            val iconColor = if (isSelected) {
                MaterialTheme.colorScheme.tertiary
            } else {
                MaterialTheme.colorScheme.outline
            }
            val label = stringResource(item.label)
            NavigationBarItem(
                icon = { Icon(item.icon, label, tint = iconColor) },
                label = { Text(label, maxLines = 1) },
                selected = isSelected,
                onClick = {
                    item.click(navController)
                },
            )
        }
    }
}
