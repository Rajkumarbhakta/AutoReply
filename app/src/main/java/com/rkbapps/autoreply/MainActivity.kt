package com.rkbapps.autoreply

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.compose.rememberNavController
import com.rkbapps.autoreply.navigation.MainNavGraph
import com.rkbapps.autoreply.ui.composables.BottomNavigation
import com.rkbapps.autoreply.ui.theme.AutoReplyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AutoReplyTheme(darkTheme = false) {
                val navController = rememberNavController()
                Surface {
                    Scaffold(
                        bottomBar = {
                            BottomNavigation(navController = navController)
                        }
                    ) {
                        Box(
                            modifier = Modifier.padding(
                                bottom = it.calculateBottomPadding(),
                                start = it.calculateLeftPadding(LayoutDirection.Ltr),
                                end = it.calculateRightPadding(LayoutDirection.Ltr)
                            )
                        ) {
                            MainNavGraph(navController = navController)
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val enabled = NotificationManagerCompat.getEnabledListenerPackages(this)
            .contains(BuildConfig.APPLICATION_ID)
    }

}