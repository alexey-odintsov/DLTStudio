package com.alekso.dltstudio.ui.logs

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.IconToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.alekso.dltstudio.ui.HorizontalDivider
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun LogsToolbar(
    toolbarFatalChecked: Boolean,
    toolbarErrorChecked: Boolean,
    toolbarWarningChecked: Boolean,
    logPreviewChecked: Boolean,
    updateToolbarFatalCheck: (Boolean) -> Unit,
    updateToolbarErrorCheck: (Boolean) -> Unit,
    updateToolbarWarningCheck: (Boolean) -> Unit,
    updateToolbarLogPreviewCheck: (Boolean) -> Unit
) {
    // Toolbar
    Row {
        IconToggleButton(modifier = Modifier.size(32.dp),
            checked = toolbarFatalChecked,
            onCheckedChange = { updateToolbarFatalCheck.invoke(it) }) {
            Image(
                painterResource("icon_f.xml"),
                contentDescription = "Enable fatal logs highlight",
                modifier = Modifier.padding(6.dp),
                colorFilter = if (toolbarFatalChecked) ColorFilter.tint(Color.Red) else ColorFilter.tint(
                    Color.Gray
                )
            )
        }
        IconToggleButton(modifier = Modifier.size(32.dp),
            checked = toolbarErrorChecked,
            onCheckedChange = { updateToolbarErrorCheck.invoke(it) }) {
            Image(
                painterResource("icon_e.xml"),
                contentDescription = "Enable error logs highlight",
                modifier = Modifier.padding(6.dp),
                colorFilter = if (toolbarErrorChecked) ColorFilter.tint(Color.Red) else ColorFilter.tint(
                    Color.Gray
                )
            )
        }
        IconToggleButton(modifier = Modifier.size(32.dp),
            checked = toolbarWarningChecked,
            onCheckedChange = { updateToolbarWarningCheck.invoke(it) }) {
            Image(
                painterResource("icon_w.xml"),
                contentDescription = "Enable warning logs highlight",
                modifier = Modifier.padding(6.dp),
                colorFilter = if (toolbarWarningChecked)
                    ColorFilter.tint(
                        Color(0xE7, 0x62, 0x29)
                    ) else ColorFilter.tint(Color.Gray)
            )
        }
        HorizontalDivider(modifier = Modifier.height(32.dp))
        IconToggleButton(modifier = Modifier.size(32.dp),
            checked = logPreviewChecked,
            onCheckedChange = { updateToolbarLogPreviewCheck.invoke(it) }) {
            Image(
                painterResource("icon_dlt_info.xml"),
                contentDescription = "Show log preview panel",
                modifier = Modifier.padding(6.dp),
                colorFilter = if (logPreviewChecked)
                    ColorFilter.tint(Color.Blue) else ColorFilter.tint(Color.Gray)
            )
        }
    }
}