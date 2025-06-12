package com.rkbapps.autoreply.ui.composables

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.rkbapps.autoreply.utils.BottomNavigationItems
import com.rkbapps.autoreply.utils.bottomNavigationItems


@Composable
fun BottomNavigation(
    modifier: Modifier = Modifier,
    navigationItems: List<BottomNavigationItems> = bottomNavigationItems,
    navController: NavHostController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val destination =
        navigationItems.any { currentDestination?.hasRoute(route = it.route::class) == true }

    if (destination) {
        NavigationBar(
            modifier = modifier
        ) {
            navigationItems.forEachIndexed { _, bottomNavigationItem ->

                val isSelected =
                    currentDestination?.hierarchy?.any { it.hasRoute(route = bottomNavigationItem.route::class) } == true

                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = if (isSelected) bottomNavigationItem.icon else bottomNavigationItem.unselectedIcon,
                            contentDescription = bottomNavigationItem.title
                        )
                    },
                    label = { Text(bottomNavigationItem.title) },
                    selected = isSelected,
                    onClick = {
                        navController.navigate(bottomNavigationItem.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }


}