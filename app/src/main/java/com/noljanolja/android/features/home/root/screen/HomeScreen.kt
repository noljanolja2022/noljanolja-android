package com.noljanolja.android.features.home.root.screen

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.noljanolja.android.common.composable.FullSizeWithBottomSheet
import com.noljanolja.android.features.home.component.HomeFloatingActionButton
import com.noljanolja.android.features.home.mypage.screen.MyPageScreen
import com.noljanolja.android.features.home.require_login.RequireLoginBottomSheet
import com.noljanolja.android.features.home.utils.click
import com.noljanolja.android.features.home.utils.isNavItemSelect
import com.noljanolja.android.util.getErrorMessage
import com.noljanolja.android.util.showToast
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmStateChange = { it != ModalBottomSheetValue.HalfExpanded },
        skipHalfExpanded = true,
    )

    LaunchedEffect(key1 = viewModel.showRequireLoginPopupEvent) {
        launch {
            viewModel.showRequireLoginPopupEvent.collect {
                if (it) {
                    modalSheetState.animateTo(ModalBottomSheetValue.Expanded)
                } else {
                    modalSheetState.animateTo(ModalBottomSheetValue.Hidden)
                }
            }
        }
    }

    LaunchedEffect(key1 = viewModel.errorFlow) {
        launch {
            viewModel.errorFlow.collect {
                context.showToast(context.getErrorMessage(it))
            }
        }
    }

    val navController = rememberNavController()
    FullSizeWithBottomSheet(
        modalSheetState = modalSheetState,
        sheetContent = {
            RequireLoginBottomSheet(
                modalSheetState = modalSheetState,
                onGoToLogin = {
                    viewModel.handleEvent(HomeEvent.LoginOrVerifyEmail)
                },
            )
        },
    ) {
        Scaffold(
            floatingActionButton = { HomeFloatingActionButton(navController = navController) },
            isFloatingActionButtonDocked = true,
            floatingActionButtonPosition = FabPosition.Center,
            bottomBar = {
                BottomAppBar(
                    modifier = Modifier,
                    backgroundColor = Color.White,
                    cutoutShape = RoundedCornerShape(50),
                    // backgroundColor = Color.White,
                    elevation = 22.dp,
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
                startDestination = HomeNavigationItem.HomeItem.route,
                modifier = Modifier.padding(contentPadding),
            ) {
                addNavigationGraph()
            }
        }
    }
}

private fun NavGraphBuilder.addNavigationGraph() {
    composable(HomeNavigationItem.MenuItem.route) {
        Text(
            "MenuItem",
            modifier = Modifier
                .fillMaxSize()
                .padding(100.dp),
        )
    }
    composable(HomeNavigationItem.HomeItem.route) {
        Text(
            "HomeItem",
            modifier = Modifier
                .fillMaxSize()
                .padding(100.dp),
        )
    }
    composable(HomeNavigationItem.WalletItem.route) {
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
        HomeNavigationItem.MenuItem,
        HomeNavigationItem.HomeItem,
        HomeNavigationItem.WalletItem,
        HomeNavigationItem.ShopItem,
        HomeNavigationItem.UserItem,
    )
    BottomNavigation(
        elevation = 0.dp,
        backgroundColor = Color.White,
    ) {
        items.forEach { item ->
            if (item != HomeNavigationItem.WalletItem) {
                val isSelected = item.isNavItemSelect(navController = navController)
                val iconId = item.icon
                val iconColor = if (isSelected) {
                    MaterialTheme.colorScheme.tertiary
                } else {
                    MaterialTheme.colorScheme.outline
                }
                NavigationBarItem(
                    icon = {
                        Icon(
                            painterResource(id = iconId),
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
            } else {
                Spacer(modifier = Modifier.weight(1F))
            }
        }
    }
}
