package com.rkbapps.autoreply.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.ChatBubble
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.rkbapps.autoreply.navigation.NavigationRoutes

sealed class BottomNavigationItems(
    val title: String,
    val icon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: NavigationRoutes
) {
    data object Home : BottomNavigationItems(
        title = "Home",
        icon = Icons.Default.Home,
        unselectedIcon = Icons.Outlined.Home,
        route = NavigationRoutes.Home
    )

    data object Rules : BottomNavigationItems(
        title = "Auto Reply",
        icon = Icons.Default.ChatBubble,
        unselectedIcon = Icons.Outlined.ChatBubble,
        route = NavigationRoutes.AutoReply
    )

    data object SentHistory : BottomNavigationItems(
        title = "Sent",
        icon = Icons.AutoMirrored.Filled.Send,
        unselectedIcon = Icons.AutoMirrored.Outlined.Send,
        route = NavigationRoutes.Sent
    )

    data object Settings : BottomNavigationItems(
        title = "Settings",
        icon = Icons.Default.Settings,
        unselectedIcon = Icons.Outlined.Settings,
        route = NavigationRoutes.Settings
    )
}

val bottomNavigationItems = listOf(
    BottomNavigationItems.Home,
    BottomNavigationItems.Rules,
//    BottomNavigationItems.SentHistory,
    BottomNavigationItems.Settings
)