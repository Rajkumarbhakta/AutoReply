package com.rkbapps.autoreply.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.rkbapps.autoreply.screens.HomeScreen

@Composable
fun MainNavGraph(navController:NavHostController) {
    NavHost(navController = navController, startDestination = Home){
        composable<Home> {
            HomeScreen(navController = navController)
        }
    }
}