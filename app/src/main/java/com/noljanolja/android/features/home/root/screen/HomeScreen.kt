package com.noljanolja.android.features.home.root.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.noljanolja.android.features.home.menu.screen.MenuScreen
import com.noljanolja.android.features.home.mypage.screen.MyPageScreen
import com.noljanolja.android.features.home.utils.click
import com.noljanolja.android.features.home.utils.isNavItemSelect
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
            BottomAppBar(
                modifier = Modifier,
                containerColor = MaterialTheme.colorScheme.onPrimary,
                tonalElevation = 22.dp,
            ) {
                HomeBottomBar(navController, onItemClick = {
                    viewModel.handleEvent(
                        HomeEvent.ChangeNavigationItem(
                            item = it,
                            onChange = {
                                it.click(navController)
                            },
                        ),
                    )
                })
            }
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
        Text(
            "HomeItem",
            modifier = Modifier
                .fillMaxSize()
                .padding(100.dp),
        )
    }
    composable(HomeNavigationItem.PlayItem.route) {
        Text(
            "WalletItem",
            modifier = Modifier
                .fillMaxSize()
                .padding(100.dp),
        )
    }
    composable(HomeNavigationItem.ShopItem.route) {
        Text(
            "ShopItem",
            modifier = Modifier
                .fillMaxSize()
                .padding(100.dp),
        )
    }
    composable(HomeNavigationItem.UserItem.route) {
        MyPageScreen()
    }
}

@Composable
fun HomeBottomBar(
    navController: NavHostController,
    onItemClick: (HomeNavigationItem) -> Unit,
) {
    val items = listOf(
        HomeNavigationItem.ChatItem,
        HomeNavigationItem.CelebrationItem,
        HomeNavigationItem.PlayItem,
        HomeNavigationItem.ShopItem,
        HomeNavigationItem.UserItem,
    )
    BottomAppBar(
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
            NavigationBarItem(
                icon = {
                    Icon(
                        item.icon,
                        null,
                        tint = iconColor,
                        modifier = Modifier.size(30.dp),
                    )
                },
                selected = isSelected,
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.White,
                ),
                onClick = {
                    onItemClick(item)
                },
            )
        }
    }
}
