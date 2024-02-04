package com.alekso.dltstudio.logs

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.alekso.dltstudio.ui.HorizontalDivider
import com.alekso.dltstudio.ui.ToggleImageButton

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
        ToggleImageButton(
            checkedState = toolbarFatalChecked,
            iconName = "icon_f.xml",
            title = "Enable fatal logs highlight",
            checkedTintColor = Color.Red,
            updateCheckedState = updateToolbarFatalCheck
        )
        ToggleImageButton(
            checkedState = toolbarErrorChecked,
            iconName = "icon_e.xml",
            title = "Enable error logs highlight",
            checkedTintColor = Color.Red,
            updateCheckedState = updateToolbarErrorCheck
        )
        ToggleImageButton(
            checkedState = toolbarWarningChecked,
            iconName = "icon_w.xml",
            title = "Enable warning logs highlight",
            checkedTintColor = Color(0xE7, 0x62, 0x29),
            updateCheckedState = updateToolbarWarningCheck
        )

        HorizontalDivider(modifier = Modifier.height(32.dp))

        ToggleImageButton(
            checkedState = logPreviewChecked,
            iconName = "icon_dlt_info.xml",
            title = "Enable error logs highlight",
            checkedTintColor = Color.Blue,
            updateCheckedState = updateToolbarLogPreviewCheck
        )
    }
}