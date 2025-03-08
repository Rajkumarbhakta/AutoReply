package com.rkbapps.autoreply

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.compose.rememberNavController
import com.rkbapps.autoreply.navigation.MainNavGraph
import com.rkbapps.autoreply.ui.theme.AutoReplyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AutoReplyTheme {
                val navController = rememberNavController()
                MainNavGraph(navController = navController)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val enabled = NotificationManagerCompat.getEnabledListenerPackages(this).contains(
            BuildConfig.APPLICATION_ID
        )
    }

}