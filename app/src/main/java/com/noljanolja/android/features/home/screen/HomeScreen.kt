package com.noljanolja.android.features.home.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.BottomNavigation
import androidx.compose.material.FabPosition
import androidx.compose.material.Scaffold
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.noljanolja.android.R

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                modifier = Modifier
                    .size(68.dp),
                onClick = {
                    onNavigationItemClick(navController, HomeNavigationItem.HomeItem3)
                }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_wallet),
                    contentDescription = null,
                    modifier = Modifier.size(36.dp),
                )
            }
        },
        isFloatingActionButtonDocked = true,
        floatingActionButtonPosition = FabPosition.Center,
        bottomBar = {
            BottomAppBar(
                modifier = Modifier,
                backgroundColor = Color.White,
                cutoutShape = RoundedCornerShape(50),
                //backgroundColor = Color.White,
                elevation = 22.dp
            ) {
                HomeBottomBar(navController)
            }
        }
    ) { contentPadding ->
        NavHost(
            navController = navController,
            startDestination = HomeNavigationItem.HomeItem2.route,
            modifier = Modifier.padding(contentPadding)
        ) {
            composable(HomeNavigationItem.HomeItem1.route) {
                Text(
                    "Home1",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(100.dp)
                )
            }
            composable(HomeNavigationItem.HomeItem2.route) {
                Text(
                    "Home2",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(100.dp)
                )
            }
            composable(HomeNavigationItem.HomeItem3.route) {
                Text(
                    "Home3",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(100.dp)
                        .clickable { viewModel.logOut() }
                )
            }
            composable(HomeNavigationItem.HomeItem4.route) {
                Text(
                    "Home4",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(100.dp)
                )
            }
        }
    }
}

@Composable
fun HomeBottomBar(navController: NavHostController) {
    val items = listOf(
        HomeNavigationItem.HomeItem1,
        HomeNavigationItem.HomeItem2,
        null,
        HomeNavigationItem.HomeItem3,
        HomeNavigationItem.HomeItem4,
    )
    BottomNavigation(
        elevation = 0.dp,
        backgroundColor = Color.White
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        items.forEach { item ->
            item?.let {
                val isSelected =
                    currentDestination?.hierarchy?.any { it.route == item.route } == true
                val iconId = item.icon
                val iconColor = if (isSelected) {
                    MaterialTheme.colorScheme.secondary
                } else {
                    colorResource(id = R.color.secondary_text_color)
                }
                NavigationBarItem(
                    icon = {
                        Icon(
                            painterResource(id = iconId),
                            null,
                            tint = iconColor,
                            modifier = Modifier.size(30.dp)
                        )
                    },
                    selected = isSelected,
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.White
                    ),
                    onClick = {
                        onNavigationItemClick(navController, item)
                    }
                )
            } ?: Spacer(modifier = Modifier.weight(1F))
        }
    }
}

private fun onNavigationItemClick(
    navController: NavHostController,
    item: HomeNavigationItem
) {
    navController.navigate(item.route) {
        // Pop up to the start destination of the graph to
        // avoid building up a large stack of destinations
        // on the back stack as users select items
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        // Avoid multiple copies of the same destination when
        // reselecting the same item
        launchSingleTop = true
        // Restore state when reselecting a previously selected item
        restoreState = true
    }
}
