package com.alekso.dltstudio.uicomponents

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomCheckbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    colors: CheckboxColors = CheckboxDefaults.colors()
) {
    CompositionLocalProvider(
        // disable 48dp padding (minimumInteractiveComponentSize)
        LocalMinimumInteractiveComponentEnforcement provides false,
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = if (onCheckedChange != null) {
                { onCheckedChange(!checked) }
            } else null,
            interactionSource = interactionSource,
            enabled = enabled,
            colors = colors,
            modifier = modifier.scale(0.7f) // make hardcoded size smaller
                .size(16.dp)
                .requiredSize(16.dp).padding(0.dp)

        )
    }
}

