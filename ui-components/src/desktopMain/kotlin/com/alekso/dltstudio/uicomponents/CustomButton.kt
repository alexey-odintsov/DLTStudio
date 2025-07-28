package com.alekso.dltstudio.uicomponents

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.alekso.dltstudio.theme.SystemTheme
import com.alekso.dltstudio.theme.ThemeManager

@Composable
fun CustomButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(4.dp),// ButtonDefaults.shape,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 8.dp,
        vertical = 4.dp,
    ),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable RowScope.() -> Unit
) {
    @Suppress("NAME_SHADOWING")
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
    val containerColor =
        if (enabled) colors.containerColor else colors.disabledContainerColor //colors.containerColor(enabled)
    val contentColor =
        if (enabled) colors.contentColor else colors.disabledContentColor //colors.contentColor(enabled)
    Surface(
        onClick = onClick,
        modifier = modifier.semantics { role = Role.Button },
        enabled = enabled,
        shape = shape,
        color = containerColor,
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
        contentColor = contentColor,
        border = border,
        interactionSource = interactionSource
    ) {
        val mergedStyle = LocalTextStyle.current.merge(MaterialTheme.typography.labelLarge)
        CompositionLocalProvider(
            LocalContentColor provides contentColor,
            LocalTextStyle provides mergedStyle,
        ) {
            Row(
                Modifier.defaultMinSize(
                    minWidth = 40.dp,//ButtonDefaults.MinWidth,
                    minHeight = 20.dp,//ButtonDefaults.MinHeight
                ).padding(contentPadding),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                content = content
            )
        }
    }
}

@Preview
@Composable
fun PreviewCustomButton() {
    Row {
        ThemeManager.CustomTheme(SystemTheme(true)) {
            PreviewButtonWithParameters()
        }
        ThemeManager.CustomTheme(SystemTheme(false)) {
            PreviewButtonWithParameters()
        }
    }
}

@Composable
fun PreviewButtonWithParameters() {
    Column(Modifier.padding(4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(Modifier.padding(4.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            CustomButton(onClick = {}) {
                Text("Custom Button")
            }
            CustomButton(enabled = false, onClick = {}) {
                Text("Disabled")
            }
        }
        Row(Modifier.padding(4.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Button(onClick = {}) {
                Text("Button")
            }
            Button(enabled = false, onClick = {}) {
                Text("Button")
            }
        }
        Row(Modifier.padding(4.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            FilledTonalButton(onClick = {}) {
                Text("FilledTonalButton")
            }
            FilledTonalButton(enabled = false, onClick = {}) {
                Text("Disabled")
            }
        }
        Row(Modifier.padding(4.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            OutlinedButton(onClick = { }) {
                Text("Outlined")
            }
            OutlinedButton(enabled = false, onClick = { }) {
                Text("Outlined")
            }
        }
        Row(Modifier.padding(4.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            ElevatedButton(onClick = { }) {
                Text("Elevated")
            }
            ElevatedButton(enabled = false, onClick = { }) {
                Text("Elevated")
            }
        }
    }
}