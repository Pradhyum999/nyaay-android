package com.nyaayneeti.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun NyaayTheme(
    darkTheme: Boolean = true, // Default to Nyaay signature Obsidian dark aesthetic
    content: @Composable () -> Unit
) {
    val palette = if (darkTheme) DarkPalette else LightPalette

    val colorScheme = if (darkTheme) darkColorScheme(
        primary = palette.primary,
        onPrimary = OnPrimary,
        primaryContainer = palette.primaryContainer,
        onPrimaryContainer = palette.primary,
        secondary = palette.primarySecondary,
        onSecondary = OnPrimary,
        background = palette.background,
        onBackground = palette.textMain,
        surface = palette.surface,
        onSurface = palette.textMain,
        surfaceVariant = palette.surfaceElevated,
        onSurfaceVariant = palette.textSub,
        outline = palette.border
    ) else lightColorScheme(
        primary = palette.primary,
        onPrimary = OnPrimary,
        primaryContainer = palette.primaryContainer,
        onPrimaryContainer = palette.primary,
        secondary = palette.primarySecondary,
        onSecondary = OnPrimary,
        background = palette.background,
        onBackground = palette.textMain,
        surface = palette.surface,
        onSurface = palette.textMain,
        surfaceVariant = palette.surfaceElevated,
        onSurfaceVariant = palette.textSub,
        outline = palette.border
    )

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

    CompositionLocalProvider(LocalNyaayPalette provides palette) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

/** Resolves a stored theme preference ("system" | "dark" | "light") to a boolean. */
fun resolveDarkTheme(mode: String, systemDark: Boolean): Boolean = when (mode) {
    "light" -> false
    "dark" -> true
    else -> systemDark
}

