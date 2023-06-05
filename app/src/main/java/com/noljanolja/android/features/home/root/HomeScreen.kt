package com.noljanolja.android.features.home.root

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.noljanolja.android.features.home.conversations.ConversationsScreen
import com.noljanolja.android.features.home.play.playlist.PlayListScreen
import com.noljanolja.android.features.home.utils.click
import com.noljanolja.android.features.home.utils.isNavItemSelect
import com.noljanolja.android.features.home.wallet.WalletScreen
import com.noljanolja.android.features.shop.ShopScreen
import com.noljanolja.android.ui.composable.FullSizeUnderConstruction
import com.noljanolja.android.ui.theme.colorBackground
import com.noljanolja.android.util.getErrorMessage
import com.noljanolja.android.util.showToast
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = getViewModel(),
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
    val readAllConversations by viewModel.readAllConversations.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = {
            HomeBottomBar(
                navController,
                isReadAllConversations = readAllConversations
            )
        },
    ) { contentPadding ->
        NavHost(
            navController = navController,
            startDestination = HomeNavigationItem.ChatItem.route,
            modifier = Modifier.padding(contentPadding),
        ) {
            addNavigationGraph()
        }
    }
}

private fun NavGraphBuilder.addNavigationGraph() {
    composable(HomeNavigationItem.ChatItem.route) {
        ConversationsScreen()
    }
    composable(HomeNavigationItem.WatchItem.route) {
        PlayListScreen()
    }
    composable(HomeNavigationItem.WalletItem.route) {
        WalletScreen()
    }
    composable(HomeNavigationItem.StoreItem.route) {
        ShopScreen()
    }
    composable(HomeNavigationItem.NewsItem.route) {
        FullSizeUnderConstruction()
    }
}

@Composable
fun HomeBottomBar(
    navController: NavHostController,
    isReadAllConversations: Boolean,
) {
    val items = listOf(
        HomeNavigationItem.ChatItem,
        HomeNavigationItem.WatchItem,
        HomeNavigationItem.WalletItem,
        HomeNavigationItem.StoreItem,
        HomeNavigationItem.NewsItem,
    )
    NavigationBar(
        tonalElevation = 0.dp,
        containerColor = MaterialTheme.colorBackground(),
    ) {
        items.forEachIndexed { index, item ->
            val isSelected = item.isNavItemSelect(navController = navController)
            val label = stringResource(item.label)
            NavigationBarItem(
                icon = {
                    if (
                        index != 0 || isReadAllConversations
                    ) {
                        Icon(
                            item.icon, label, modifier = Modifier
                                .padding(2.dp)
                                .size(24.dp)
                        )
                    } else {
                        Box {
                            Icon(item.icon, label)
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(
                                        MaterialTheme.colorScheme.error.copy(alpha = 0.5F)
                                    )
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(2.dp)
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(
                                        MaterialTheme.colorScheme.error
                                    )
                            )
                        }
                    }
                },
                label = { Text(label, maxLines = 1) },
                selected = isSelected,
                onClick = {
                    item.click(navController)
                },
                colors = with(MaterialTheme.colorScheme) {
                    NavigationBarItemDefaults.colors(
                        selectedIconColor = primary,
                        selectedTextColor = primary,
                        unselectedIconColor = outline,
                        unselectedTextColor = outline,
                        indicatorColor = MaterialTheme.colorScheme.background
                    )
                }
            )
        }
    }
}
