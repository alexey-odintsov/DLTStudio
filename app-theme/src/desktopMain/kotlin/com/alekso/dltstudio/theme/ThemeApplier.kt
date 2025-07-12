package com.alekso.dltstudio.theme

import androidx.compose.foundation.DarkDefaultContextMenuRepresentation
import androidx.compose.foundation.LightDefaultContextMenuRepresentation
import androidx.compose.foundation.LocalContextMenuRepresentation
import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.dp


/**
 * Applies a theme on top of MaterialTheme
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ThemeApplier(
    theme: Theme,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = theme.colorScheme(),
        typography = theme.typography(),
        shapes = theme.shapes()
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

        val contextMenuRepresentation = if (isSystemInDarkTheme()) {
            DarkDefaultContextMenuRepresentation
        } else {
            LightDefaultContextMenuRepresentation
        }

        Surface(tonalElevation = 0.dp) { // defines bg tone from primary color and dark theme bg
            CompositionLocalProvider(
                LocalScrollbarStyle provides scrollbar,
                // disable 48dp padding (minimumInteractiveComponentSize)
                LocalMinimumInteractiveComponentEnforcement provides false,
                LocalContextMenuRepresentation provides contextMenuRepresentation,
                content = content
            )
        }
    }
}
