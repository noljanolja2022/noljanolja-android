package com.noljanolja.android.ui.screen.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@Composable
fun HomeScreen() {
    val navController = rememberNavController()
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                onNavigationItemClick(navController, HomeNavigationItem.HomeItem3)
            }) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        },
        isFloatingActionButtonDocked = true,
        floatingActionButtonPosition = FabPosition.Center,
        bottomBar = { HomeBottomBar(navController) },
    ) { contentPadding ->
        NavHost(
            navController = navController,
            startDestination = HomeNavigationItem.HomeItem2.route,
            modifier = Modifier.padding(contentPadding)
        ) {
            composable(HomeNavigationItem.HomeItem1.route) {
                Text(
                    "Home1", modifier = Modifier
                        .fillMaxSize()
                        .padding(100.dp)
                )
            }
            composable(HomeNavigationItem.HomeItem2.route) {
                Text(
                    "Home2", modifier = Modifier
                        .fillMaxSize()
                        .padding(100.dp)
                )

            }
            composable(HomeNavigationItem.HomeItem3.route) {
                Text(
                    "Home3", modifier = Modifier
                        .fillMaxSize()
                        .padding(100.dp)
                )

            }
            composable(HomeNavigationItem.HomeItem4.route) {
                Text(
                    "Home4", modifier = Modifier
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
        HomeNavigationItem.HomeItem3,
        HomeNavigationItem.HomeItem4,
    )
    BottomAppBar(
        actions = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            items.forEach { item ->
                val isSelected =
                    currentDestination?.hierarchy?.any { it.route == item.route } == true
                val label = stringResource(item.label)
                // TODO update icon
                var icon = Icons.Default.Delete
                var iconColor = MaterialTheme.colorScheme.onSurfaceVariant
                if (isSelected) {
                    icon = Icons.Default.Home
                    iconColor = MaterialTheme.colorScheme.primary
                }

                NavigationBarItem(icon = { Icon(icon, label, tint = iconColor) },
                    label = { Text(label, maxLines = 1) },
                    selected = isSelected,
                    onClick = {
                        onNavigationItemClick(navController, item)
                    })
            }
        },
    )
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