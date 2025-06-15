package com.alekso.dltstudio.theme

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.DarkDefaultContextMenuRepresentation
import androidx.compose.foundation.LightDefaultContextMenuRepresentation
import androidx.compose.foundation.LocalContextMenuRepresentation
import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.AssistChip
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.InputChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderState
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
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

        Surface(tonalElevation = 5.dp) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewComponents(isDark: Boolean) {
    ThemeApplier(theme = SystemTheme(isDark = isDark)) {
        Column {
            Text(if (isDark) "Dark" else "Light")
            Row {
                Button(onClick = {}) { Text("Button") }
                OutlinedButton(onClick = {}) { Text("Outlined") }
            }
            Row {
                FilledTonalButton(onClick = {}) { Text("FilledTonal") }
                ElevatedButton(onClick = {}) { Text("Elevated") }
            }
            Row {
                BasicTextField("Edit text", onValueChange = {})
                OutlinedTextField("Edit text", onValueChange = {})
            }
            BadgedBox(badge = { Text("1") }) { Text("2") }
            //BasicAlertDialog(onDismissRequest = {}) { Button(onClick = {}) { Text("OK") } }
            Row {
                Card(Modifier.padding(4.dp)) {
                    Text("Card")
                }
                ElevatedCard(Modifier.padding(4.dp)) {
                    Text("Elevated Card")
                }
                OutlinedCard(Modifier.padding(4.dp)) {
                    Text("Outlined Card")
                }
            }
            Row {
                AssistChip(onClick = {}, label = { Text("AssistChip") })
                ElevatedAssistChip(onClick = {}, label = { Text("ElevatedAssistChip") })
            }
            Row {
                FilterChip(selected = false, onClick = {}, label = { Text("FilterChip") })
                ElevatedFilterChip(selected = false, onClick = {}, label = { Text("ElevatedFilterChip") })
            }
            Row {
                InputChip(selected = false, onClick = {}, label = { Text("InputChip") })
                SuggestionChip(onClick = {}, label = { Text("SuggestionChip") })
            }
            Spacer(Modifier.height(10.dp))

            HorizontalDivider(Modifier.width(100.dp).height(1.dp))
            Row {
                Checkbox(checked = true, onCheckedChange = {})
                Checkbox(checked = false, onCheckedChange = {})
                Checkbox(enabled = false, checked = true, onCheckedChange = {})
                RadioButton(selected = true, onClick = {})
                RadioButton(selected = false, onClick = {})
                RadioButton(enabled = false, selected = false, onClick = {})
            }
            Row {
                Switch(checked = true, onCheckedChange = {})
                Switch(checked = false, onCheckedChange = {})
                Switch(enabled = false, checked = true, onCheckedChange = {})
            }
            FloatingActionButton(onClick = {}) { Text("FAB") }
//            IconButton(onClick = {}) { Icon(painter = painterResource(Res.drawable.icon_upload),"") }
            Row {
                LinearProgressIndicator(modifier = Modifier.weight(1f), progress = { 0.5f })
                Slider(modifier = Modifier.weight(1f), state = SliderState(value = 0.5f))
            }
            Row {
                Tab(modifier = Modifier.weight(1f), selected = true, onClick = {}) { Text("Tab1") }
                Tab(modifier = Modifier.weight(1f), selected = false, onClick = {}) { Text("Tab2") }
            }
//            NavigationRail { Text("NavigationRail") }
        }
    }
}

@Preview
@Composable
fun PreviewDarkAndLightTheme() {
    Row(Modifier.fillMaxSize()) {
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
