package com.alekso.dltstudio

import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


const val BODY1_FONT_SIZE = 13f
const val BODY1_LINE_HEIGHT = BODY1_FONT_SIZE + 2
const val LOGS_FONT_SIZE = 10f
const val LOGS_LINE_HEIGHT = LOGS_FONT_SIZE + 2

@Deprecated("Use LocalSettingsUI")
val LocalLogsTextStyle = staticCompositionLocalOf { TextStyle() }

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) darkColorScheme() else lightColorScheme(),
        typography = MaterialTheme.typography.copy(
            bodyMedium = MaterialTheme.typography.bodyMedium.copy(
                fontSize = BODY1_FONT_SIZE.sp,
                lineHeight = BODY1_LINE_HEIGHT.sp
            ),
        ),
    ) {

        // Copied from DesktopTheme.jvm.kt
        val scrollbar = ScrollbarStyle(
            minimalHeight = 16.dp,
            thickness = 8.dp,
            shape = MaterialTheme.shapes.small,
            hoverDurationMillis = 300,
            unhoverColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
            hoverColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.50f)
        )

        val logsTextStyle = MaterialTheme.typography.bodyMedium.copy(
            fontSize = LocalSettingsUI.current.fontSize.sp,
            lineHeight = LOGS_LINE_HEIGHT.sp,
            fontFamily = FontFamily.Monospace,
        )

        Surface {
            CompositionLocalProvider(
                LocalScrollbarStyle provides scrollbar,
                LocalLogsTextStyle provides logsTextStyle,
                content = content
            )
        }
    }
}
