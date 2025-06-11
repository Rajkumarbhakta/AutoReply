package com.rkbapps.autoreply.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = primaryColor,
    onPrimary = Color.White,
    surface = surfaceColorDark,
    surfaceContainer = surfaceContainerDark,
    background = surfaceColorDark,
    onBackground = Color.White,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = primaryColor,
    onPrimary = Color.White,
    surface = surfaceColor,
    surfaceContainer = surfaceContainerLight,
    background = surfaceColor,
    onBackground = Color.Black,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondaryContainer = secondaryContainer,
    onSecondaryContainer = Color.Black
)


@Composable
fun AutoReplyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
