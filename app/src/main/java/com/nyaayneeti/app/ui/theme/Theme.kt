package com.nyaayneeti.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = ObsidianBackground,
    primaryContainer = GoldContainer,
    onPrimaryContainer = GoldPrimary,
    secondary = GoldSecondary,
    onSecondary = ObsidianBackground,
    background = ObsidianBackground,
    onBackground = TextMain,
    surface = ObsidianSurface,
    onSurface = TextMain,
    surfaceVariant = ObsidianSurfaceElevated,
    onSurfaceVariant = TextSub,
    outline = ObsidianBorder
)

private val LightColorScheme = lightColorScheme(
    primary = GoldSecondary,
    onPrimary = TextMain,
    background = TextMain,
    surface = TextMain,
    onBackground = ObsidianBackground,
    onSurface = ObsidianBackground
)

@Composable
fun NyaayTheme(
    darkTheme: Boolean = true, // Default to Nyaay signature Obsidian dark aesthetic
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            window.navigationBarColor = android.graphics.Color.TRANSPARENT
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
