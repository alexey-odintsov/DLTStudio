package com.alekso.dltstudio.theme

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.Chip
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


const val BODY1_FONT_SIZE = 13f
const val BODY1_LINE_HEIGHT = BODY1_FONT_SIZE + 2
const val LOGS_FONT_SIZE = 10f
const val LOGS_LINE_HEIGHT = LOGS_FONT_SIZE + 2

/**
 * Applies a theme on top of MaterialTheme
 */
@OptIn(ExperimentalMaterialApi::class)
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

        Surface(tonalElevation = 5.dp) {
            CompositionLocalProvider(
                LocalScrollbarStyle provides scrollbar,
                // disable 48dp padding (minimumInteractiveComponentSize)
                LocalMinimumInteractiveComponentEnforcement provides false,
                content = content
            )
        }
    }
}

@Composable
fun PreviewColors(isDark: Boolean) {
    ThemeApplier(theme = SystemTheme(isDark = isDark)) {
        Column {
            Text(if (isDark) "Dark" else "Light")
            Text(
                text = "onPrimary/primary",
                color = MaterialTheme.colorScheme.onPrimary,
                maxLines = 1,
                modifier = Modifier.background(color = MaterialTheme.colorScheme.primary)
            )
            Text(
                text = "onPrimaryContainer/primaryContainer",
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                maxLines = 1,
                modifier = Modifier.background(color = MaterialTheme.colorScheme.primaryContainer)
            )
            Text(
                text = "inversePrimary/primary",
                color = MaterialTheme.colorScheme.inversePrimary,
                maxLines = 1,
                modifier = Modifier.background(color = MaterialTheme.colorScheme.primary)
            )
            Text(
                text = "onSecondary/secondary",
                color = MaterialTheme.colorScheme.onSecondary,
                maxLines = 1,
                modifier = Modifier.background(color = MaterialTheme.colorScheme.secondary)
            )
            Text(
                text = "onSecondaryContainer/secondaryContainer",
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                maxLines = 1,
                modifier = Modifier.background(color = MaterialTheme.colorScheme.secondaryContainer)
            )
            Text(
                text = "onTertiary/tertiary",
                color = MaterialTheme.colorScheme.onTertiary,
                maxLines = 1,
                modifier = Modifier.background(color = MaterialTheme.colorScheme.tertiary)
            )
            Text(
                text = "onTertiaryContainer/tertiaryContainer",
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                maxLines = 1,
                modifier = Modifier.background(color = MaterialTheme.colorScheme.tertiaryContainer)
            )
            Text(
                text = "onBackground/background",
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                modifier = Modifier.background(color = MaterialTheme.colorScheme.background)
            )
            Text(
                text = "onSurface/surface",
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                modifier = Modifier.background(color = MaterialTheme.colorScheme.surface)
            )
            Text(
                text = "onSurfaceVariant/surfaceVariant",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                modifier = Modifier.background(color = MaterialTheme.colorScheme.surfaceVariant)
            )
            Text(
                text = "surfaceTint/surfaceVariant",
                color = MaterialTheme.colorScheme.surfaceTint,
                maxLines = 1,
                modifier = Modifier.background(color = MaterialTheme.colorScheme.surfaceVariant)
            )
            Text(
                text = "inverseOnSurface/inverseSurface",
                color = MaterialTheme.colorScheme.inverseOnSurface,
                maxLines = 1,
                modifier = Modifier.background(color = MaterialTheme.colorScheme.inverseSurface)
            )
            Text(
                text = "onError/error",
                color = MaterialTheme.colorScheme.onError,
                maxLines = 1,
                modifier = Modifier.background(color = MaterialTheme.colorScheme.error)
            )
            Text(
                text = "onErrorContainer/errorContainer",
                color = MaterialTheme.colorScheme.onErrorContainer,
                maxLines = 1,
                modifier = Modifier.background(color = MaterialTheme.colorScheme.errorContainer)
            )
            Text(
                text = "outline/background",
                color = MaterialTheme.colorScheme.outline,
                maxLines = 1,
                modifier = Modifier.background(color = MaterialTheme.colorScheme.background)
            )
            Text(
                text = "outlineVariant/background",
                color = MaterialTheme.colorScheme.outlineVariant,
                maxLines = 1,
                modifier = Modifier.background(color = MaterialTheme.colorScheme.background)
            )
            Text(
                text = "onPrimary/scrim",
                color = MaterialTheme.colorScheme.onPrimary,
                maxLines = 1,
                modifier = Modifier.background(color = MaterialTheme.colorScheme.scrim)
            )
            Text(
                text = "onSurface/surfaceBright",
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                modifier = Modifier.background(color = MaterialTheme.colorScheme.surfaceBright)
            )
            Text(
                text = "onSurface/surfaceDim",
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                modifier = Modifier.background(color = MaterialTheme.colorScheme.surfaceDim)
            )
            Text(
                text = "onSurface/surfaceContainer",
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                modifier = Modifier.background(color = MaterialTheme.colorScheme.surfaceContainer)
            )
            Text(
                text = "onSurface/surfaceContainerHigh",
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                modifier = Modifier.background(color = MaterialTheme.colorScheme.surfaceContainerHigh)
            )
            Text(
                text = "onSurface/surfaceContainerHighest",
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                modifier = Modifier.background(color = MaterialTheme.colorScheme.surfaceContainerHighest)
            )
            Text(
                text = "onSurface/surfaceContainerLow",
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                modifier = Modifier.background(color = MaterialTheme.colorScheme.surfaceContainerLow)
            )
            Text(
                text = "onSurface/surfaceContainerLowest",
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                modifier = Modifier.background(color = MaterialTheme.colorScheme.surfaceContainerLowest)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun PreviewComponents(isDark: Boolean) {
    ThemeApplier(theme = SystemTheme(isDark = isDark)) {
        Column {
            Text(if (isDark) "Dark" else "Light")
            Button(onClick = {}) { Text("Button") }
            OutlinedButton(onClick = {}) { Text("OutlinedButton") }
            TextButton(onClick = {}) { Text("TextButton") }
            BasicTextField(TextFieldState("Edit text"))
            BadgedBox(badge = { Text("1") }) { Text("2") }
            //BasicAlertDialog(onDismissRequest = {}) { Button(onClick = {}) { Text("OK") } }
            Card {
                Text("Card")
            }
            Chip(onClick = {}) { Text("Chip") }
            HorizontalDivider(Modifier.width(100.dp).height(1.dp))
            Checkbox(checked = true, onCheckedChange = {})
            FloatingActionButton(onClick = {}) { Text("FAB") }
//            IconButton(onClick = {}) { Icon(painter = painterResource(Res.drawable.icon_upload),"") }
            RadioButton(selected = true, onClick = {})
            LinearProgressIndicator(progress = { 0.5f })
            Slider(modifier = Modifier.size(200.dp, 20.dp), state = SliderState(value = 0.5f))
            Switch(checked = true, onCheckedChange = {})
            Tab(selected = true, onClick = {}) { Text("Tab") }
//            NavigationRail { Text("NavigationRail") }
        }
    }
}

@Preview
@Composable
fun PreviewDarkAndLightTheme() {
    Row(Modifier.fillMaxSize().background(color = Color.DarkGray)) {
        Box(Modifier.weight(1f)) {
            PreviewComponents(isDark = true)
        }
        Box(Modifier.weight(1f)) {
            PreviewComponents(isDark = false)
        }
    }
}

@Preview
@Composable
fun PreviewDarkAndLightColors() {
    Row(Modifier.fillMaxSize().background(color = Color.DarkGray)) {
        PreviewColors(isDark = true)
        PreviewColors(isDark = false)
    }
}
