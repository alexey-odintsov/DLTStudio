package com.alekso.dltstudio.logs

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.unit.dp
import com.alekso.dltstudio.logs.search.SearchState
import com.alekso.dltstudio.ui.AutoCompleteEditText
import com.alekso.dltstudio.ui.HorizontalDivider
import com.alekso.dltstudio.ui.ImageButton
import com.alekso.dltstudio.ui.ToggleImageButton

data class LogsToolbarState(
    val toolbarFatalChecked: Boolean,
    val toolbarErrorChecked: Boolean,
    val toolbarWarningChecked: Boolean,
    val toolbarWrapContentChecked: Boolean,
) {
    companion object {
        fun updateToolbarFatalCheck(state: LogsToolbarState, newValue: Boolean): LogsToolbarState {
            return state.copy(toolbarFatalChecked = newValue)
        }

        fun updateToolbarErrorCheck(state: LogsToolbarState, newValue: Boolean): LogsToolbarState {
            return state.copy(toolbarErrorChecked = newValue)
        }

        fun updateToolbarWarnCheck(state: LogsToolbarState, newValue: Boolean): LogsToolbarState {
            return state.copy(toolbarWarningChecked = newValue)
        }
        fun updateToolbarWrapContentCheck(state: LogsToolbarState, newValue: Boolean): LogsToolbarState {
            return state.copy(toolbarWrapContentChecked = newValue)
        }
    }
}

@Composable
fun LogsToolbar(
    state: LogsToolbarState,
    searchState: SearchState,
    searchAutoComplete: List<String>,
    onSearchButtonClicked: (String) -> Unit,
    updateToolbarFatalCheck: (Boolean) -> Unit,
    updateToolbarErrorCheck: (Boolean) -> Unit,
    updateToolbarWarningCheck: (Boolean) -> Unit,
    updateToolbarWrapContentCheck: (Boolean) -> Unit,
    onSearchUseRegexChanged: (Boolean) -> Unit,
    onColorFiltersClicked: () -> Unit,
) {
    // Toolbar
    Row(verticalAlignment = Alignment.CenterVertically) {
        ToggleImageButton(
            checkedState = state.toolbarFatalChecked,
            iconName = "icon_f.xml",
            title = "Enable fatal logs highlight",
            checkedTintColor = Color.Red,
            updateCheckedState = updateToolbarFatalCheck
        )
        ToggleImageButton(
            checkedState = state.toolbarErrorChecked,
            iconName = "icon_e.xml",
            title = "Enable error logs highlight",
            checkedTintColor = Color.Red,
            updateCheckedState = updateToolbarErrorCheck
        )
        ToggleImageButton(
            checkedState = state.toolbarWarningChecked,
            iconName = "icon_w.xml",
            title = "Enable warning logs highlight",
            checkedTintColor = Color(0xE7, 0x62, 0x29),
            updateCheckedState = updateToolbarWarningCheck
        )

        ImageButton(
            modifier = Modifier.size(32.dp),
            iconName = "icon_color_filters.xml",
            title = "Color filters",
            onClick = onColorFiltersClicked
        )

        HorizontalDivider(modifier = Modifier.height(32.dp))

        var text by rememberSaveable { mutableStateOf(searchState.searchText) }
        ToggleImageButton(
            checkedState = searchState.searchUseRegex,
            iconName = "icon_regex.xml",
            title = "Use Regex or Plain text search",
            checkedTintColor = Color.Blue,
            updateCheckedState = onSearchUseRegexChanged
        )

        AutoCompleteEditText(
            modifier = Modifier.width(500.dp).height(20.dp)
            .onKeyEvent { e ->
                if (e.key == Key.Enter) {
                    onSearchButtonClicked(text)
                    true
                } else {
                    false
                }
            },
            value = text,
            onValueChange = {
                text = it
            },
            items = searchAutoComplete
        )

        ImageButton(modifier = Modifier.size(32.dp),
            iconName = if (searchState.state == SearchState.State.IDLE) {
                "icon_search.xml"
            } else {
                "icon_stop.xml"
            },
            title = "Search",
            onClick = {
                onSearchButtonClicked(text)
            })

        HorizontalDivider(modifier = Modifier.height(32.dp))

        ToggleImageButton(
            checkedState = state.toolbarWrapContentChecked,
            iconName = "icon_wordwrap.xml",
            title = "Wrap content",
            checkedTintColor = Color.Blue,
            updateCheckedState = updateToolbarWrapContentCheck,
        )


    }
}

@Preview
@Composable
fun PreviewLogsToolbar() {
    LogsToolbar(
        state = LogsToolbarState(
            toolbarFatalChecked = true,
            toolbarErrorChecked = true,
            toolbarWarningChecked = true,
            toolbarWrapContentChecked = true,
        ),
        searchState = SearchState(),
        searchAutoComplete = emptyList(),
        onSearchButtonClicked = {},
        updateToolbarFatalCheck = {},
        updateToolbarErrorCheck = {},
        updateToolbarWarningCheck = {},
        updateToolbarWrapContentCheck = {},
        onSearchUseRegexChanged = {},
        onColorFiltersClicked = {}
    )
}