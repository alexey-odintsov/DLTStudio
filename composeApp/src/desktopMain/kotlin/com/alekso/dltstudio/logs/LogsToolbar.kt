package com.alekso.dltstudio.logs

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.alekso.dltstudio.ui.HorizontalDivider
import com.alekso.dltstudio.ui.ImageButton
import com.alekso.dltstudio.ui.SearchEditText
import com.alekso.dltstudio.ui.ToggleImageButton

@Composable
fun LogsToolbar(
    searchText: String,
    toolbarFatalChecked: Boolean,
    toolbarErrorChecked: Boolean,
    toolbarWarningChecked: Boolean,
    logPreviewChecked: Boolean,
    updateSearchText: (String) -> Unit,
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

        var text by rememberSaveable { mutableStateOf(searchText) }
        SearchEditText(
            modifier = Modifier.width(200.dp).height(32.dp).padding(start = 6.dp),
            value = text, onValueChange = {
                text = it
            }
        )

        ImageButton(modifier = Modifier.size(32.dp),
            iconName = "icon_search.xml",
            title = "Search",
            onClick = {
                updateSearchText(text)
            })

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

@Preview
@Composable
fun PreviewLogsToolbar() {
    LogsToolbar(
        searchText = "Test",
        toolbarFatalChecked = true,
        toolbarErrorChecked = true,
        toolbarWarningChecked = true,
        logPreviewChecked = true,
        updateSearchText = {},
        updateToolbarFatalCheck = {},
        updateToolbarErrorCheck = {},
        updateToolbarWarningCheck = {},
        updateToolbarLogPreviewCheck = {}
    )
}