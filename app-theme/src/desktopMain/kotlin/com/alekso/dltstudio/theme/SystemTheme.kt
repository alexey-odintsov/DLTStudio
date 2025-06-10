package com.alekso.dltstudio.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Default app theme that automatically applies dark mode based on the system settings.
 */
class SystemTheme(
    private val isDark: Boolean
) : Theme {

    init {
        //ButtonDefaults.shape = RoundedCornerShape(2.dp)
    }

    override fun isDark(): Boolean {
        return isDark
    }

    override fun colorScheme(): ColorScheme {
        return if (isDark) darkColorScheme() else lightColorScheme()
    }

    override fun colors(): Colors {
        return if (isDark)
            Colors(
                logRow = Color(0xff3b3b3d),
                onLogRow = Color(0xffc3c3c3),
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
                fontSize = 14.sp, // Text, Badge, Tab
                lineHeight = 16.sp,
            ),
            bodyMedium = MaterialTheme.typography.bodyMedium.copy(
                lineHeight = 14.sp,
            )
        )
    }

    @Composable
    override fun shapes(): Shapes {
        return MaterialTheme.shapes.copy(
            extraLarge = RoundedCornerShape(2.dp),
            medium = RoundedCornerShape(2.dp),
            small = RoundedCornerShape(2.dp),
            extraSmall = RoundedCornerShape(2.dp),

            // MaterialTheme.shapes.medium.copy()
        )
    }

}