package com.alekso.dltstudio.theme

import androidx.compose.runtime.Composable

object ThemeManager {
    val isDark = false
    private val currentTheme = SystemTheme(isDark)

    fun currentTheme(): Theme {
        return currentTheme
    }

    @Composable
    fun AppTheme(content: @Composable () -> Unit) {
        ThemeApplier(theme = currentTheme) {
            content()
        }
    }

    @Composable
    fun CustomTheme(theme: Theme, content: @Composable () -> Unit) {
        ThemeApplier(theme = theme) {
            content()
        }
    }
}