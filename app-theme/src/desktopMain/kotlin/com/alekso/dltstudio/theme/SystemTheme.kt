package com.alekso.dltstudio.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

/**
 * Default app theme that automatically applies dark mode based on the system settings.
 */
class SystemTheme(
    val isDark: Boolean
) : Theme {
    override fun colorScheme(): ColorScheme {
        return if (isDark) darkColorScheme() else lightColorScheme()
    }

    override fun colors(): Colors {
        return if (isDark)
            Colors(
                logRow = Color.Black,
                onLogRow = Color.White,
            )
        else
            Colors(
                logRow = Color.White,
                onLogRow = Color.Black,
            )
    }

    @Composable
    override fun typography(): Typography {
        return MaterialTheme.typography.copy(
            bodyLarge = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 14.sp // Text, Badge, Tab
            )
        )
    }

    @Composable
    override fun shapes(): Shapes {
        return MaterialTheme.shapes
    }
}