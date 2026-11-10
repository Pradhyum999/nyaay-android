package com.nyaayneeti.app.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Semantic palette. Screens reference these colors by name (e.g. ObsidianBackground, GoldPrimary);
 * the actual value switches between the Obsidian dark palette and a clean white light palette.
 */
data class NyaayPalette(
    val background: Color,
    val surface: Color,
    val surfaceElevated: Color,
    val border: Color,
    val primary: Color,
    val primarySecondary: Color,
    val primaryContainer: Color,
    val success: Color,
    val successContainer: Color,
    val urgent: Color,
    val urgentContainer: Color,
    val textMain: Color,
    val textSub: Color,
    val textFaint: Color,
)

/**
 * Color used for text / icons drawn on top of gold or green buttons.
 * Constant near-black so it stays readable in BOTH themes.
 */
val OnPrimary = Color(0xFF0B0F17)

/** Obsidian (dark) signature palette. */
val DarkPalette = NyaayPalette(
    background = Color(0xFF0B0F17),
    surface = Color(0xFF121824),
    surfaceElevated = Color(0xFF1A2234),
    border = Color(0xFF1E293B),
    primary = Color(0xFFF59E0B),
    primarySecondary = Color(0xFFD97706),
    primaryContainer = Color(0x26F59E0B),
    success = Color(0xFF10B981),
    successContainer = Color(0x2610B981),
    urgent = Color(0xFFEF4444),
    urgentContainer = Color(0x26EF4444),
    textMain = Color(0xFFF8FAFC),
    textSub = Color(0xFF94A3B8),
    textFaint = Color(0xFF64748B),
)

/** White (light) palette — high contrast, no hidden text. */
val LightPalette = NyaayPalette(
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFFFFFFF),
    surfaceElevated = Color(0xFFF1F5F9),
    border = Color(0xFFE2E8F0),
    primary = Color(0xFFB45309),
    primarySecondary = Color(0xFFD97706),
    primaryContainer = Color(0x1AF59E0B),
    success = Color(0xFF047857),
    successContainer = Color(0x1A10B981),
    urgent = Color(0xFFDC2626),
    urgentContainer = Color(0x1AEF4444),
    textMain = Color(0xFF0F172A),
    textSub = Color(0xFF475569),
    textFaint = Color(0xFF64748B),
)

val LocalNyaayPalette = staticCompositionLocalOf { DarkPalette }

// Backward-compatible accessors (existing code uses these names unchanged).
val ObsidianBackground: Color @Composable get() = LocalNyaayPalette.current.background
val ObsidianSurface: Color @Composable get() = LocalNyaayPalette.current.surface
val ObsidianSurfaceElevated: Color @Composable get() = LocalNyaayPalette.current.surfaceElevated
val ObsidianBorder: Color @Composable get() = LocalNyaayPalette.current.border
val GoldPrimary: Color @Composable get() = LocalNyaayPalette.current.primary
val GoldSecondary: Color @Composable get() = LocalNyaayPalette.current.primarySecondary
val GoldContainer: Color @Composable get() = LocalNyaayPalette.current.primaryContainer
val GreenSuccess: Color @Composable get() = LocalNyaayPalette.current.success
val GreenContainer: Color @Composable get() = LocalNyaayPalette.current.successContainer
val RedUrgent: Color @Composable get() = LocalNyaayPalette.current.urgent
val RedContainer: Color @Composable get() = LocalNyaayPalette.current.urgentContainer
val TextMain: Color @Composable get() = LocalNyaayPalette.current.textMain
val TextSub: Color @Composable get() = LocalNyaayPalette.current.textSub
val TextFaint: Color @Composable get() = LocalNyaayPalette.current.textFaint

