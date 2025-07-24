package com.alekso.dltstudio.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

internal val LocalAppColor = staticCompositionLocalOf { ThemeManager.currentTheme().colors() }

object AppTheme {
    val colors: Colors
        @Composable @ReadOnlyComposable get() = LocalAppColor.current
}

data class Colors(
    val logRow: Color,
    val onLogRow: Color,
)