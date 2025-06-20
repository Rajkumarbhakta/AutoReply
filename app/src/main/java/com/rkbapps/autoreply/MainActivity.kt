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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.rkbapps.autoreply.data.PreferenceManager
import com.rkbapps.autoreply.navigation.MainNavGraph
import com.rkbapps.autoreply.ui.composables.BottomNavigation
import com.rkbapps.autoreply.ui.theme.AutoReplyTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferenceManager: PreferenceManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val darkTheme = preferenceManager.isDarkThemeEnabledFlow.stateIn(
            lifecycleScope,
            SharingStarted.Lazily,
            false
        )

        setContent {
            val isDarkThemeEnabled = darkTheme.collectAsStateWithLifecycle()

            AutoReplyTheme(
                darkTheme = isDarkThemeEnabled.value,
                dynamicColor = false
            ) {
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