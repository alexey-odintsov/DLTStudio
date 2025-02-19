package com.alekso.dltstudio

import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
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
    /*darkTheme: Boolean = isSystemInDarkTheme(),*/ // todo: Support dark theme
    content: @Composable () -> Unit
) {
    MaterialTheme(
        /* colors = if (darkTheme) darkColors() else lightColors(),*/ // todo: Support dark theme
        typography = MaterialTheme.typography.copy(
            body1 = MaterialTheme.typography.body1.copy(
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
            unhoverColor = MaterialTheme.colors.onSurface.copy(alpha = 0.12f),
            hoverColor = MaterialTheme.colors.onSurface.copy(alpha = 0.50f)
        )

        val logsTextStyle = MaterialTheme.typography.body1.copy(
            fontSize = LocalSettingsUI.current.fontSize.sp,
            lineHeight = LOGS_LINE_HEIGHT.sp,
            fontFamily = FontFamily.Monospace,
        )

        CompositionLocalProvider(
            LocalScrollbarStyle provides scrollbar,
            LocalLogsTextStyle provides logsTextStyle,
            content = content
        )
    }
}
