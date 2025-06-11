package com.rkbapps.autoreply.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.rkbapps.autoreply.navigation.NavigationRoutes.AddEditAutoReply
import com.rkbapps.autoreply.navigation.NavigationRoutes.Home
import com.rkbapps.autoreply.ui.screens.ShowHtmlTextScreen
import com.rkbapps.autoreply.ui.screens.addeditautoreply.addEdit.AddEditAutoReplyScreen
import com.rkbapps.autoreply.ui.screens.help_center.HelpCenterScreen
import com.rkbapps.autoreply.ui.screens.history.SentAutoRepliesScreen
import com.rkbapps.autoreply.ui.screens.home.HomeScreen
import com.rkbapps.autoreply.ui.screens.rules.AutoReplyRulesScreen
import com.rkbapps.autoreply.ui.screens.settings.SettingsScreen

@Composable
fun MainNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Home) {
        composable<Home> {
            HomeScreen(navController = navController)
        }
        composable<AddEditAutoReply> {
            AddEditAutoReplyScreen(navController = navController)
        }
        composable<NavigationRoutes.AutoReply> {
            AutoReplyRulesScreen(navController = navController)
        }
        composable<NavigationRoutes.Settings> {
            SettingsScreen(navController = navController)
        }
        composable<NavigationRoutes.Sent> {
            SentAutoRepliesScreen(navController = navController)
        }
        composable<NavigationRoutes.HelpCenter> {
            HelpCenterScreen(navController)
        }
        composable<NavigationRoutes.ShowHtmlText> {
            val data = it.toRoute<NavigationRoutes.ShowHtmlText>()
            ShowHtmlTextScreen(
                navController = navController,
                data = data
            )
        }

    }
}