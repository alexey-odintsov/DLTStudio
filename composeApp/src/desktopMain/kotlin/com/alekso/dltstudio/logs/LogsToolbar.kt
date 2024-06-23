package com.alekso.dltstudio.logs

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
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
import com.alekso.dltstudio.logs.search.SearchType
import com.alekso.dltstudio.ui.AutoCompleteEditText
import com.alekso.dltstudio.ui.HorizontalDivider
import com.alekso.dltstudio.ui.ImageButton
import com.alekso.dltstudio.ui.ToggleImageButton
import dtlstudio.composeapp.generated.resources.Res
import dtlstudio.composeapp.generated.resources.icon_color_filters
import dtlstudio.composeapp.generated.resources.icon_comments
import dtlstudio.composeapp.generated.resources.icon_e
import dtlstudio.composeapp.generated.resources.icon_f
import dtlstudio.composeapp.generated.resources.icon_marked_logs
import dtlstudio.composeapp.generated.resources.icon_regex
import dtlstudio.composeapp.generated.resources.icon_search
import dtlstudio.composeapp.generated.resources.icon_stop
import dtlstudio.composeapp.generated.resources.icon_w
import dtlstudio.composeapp.generated.resources.icon_wordwrap

data class LogsToolbarState(
    val toolbarFatalChecked: Boolean,
    val toolbarErrorChecked: Boolean,
    val toolbarWarningChecked: Boolean,
    val toolbarCommentsChecked: Boolean,
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
        fun updateToolbarCommentsCheck(state: LogsToolbarState, newValue: Boolean): LogsToolbarState {
            return state.copy(toolbarCommentsChecked = newValue)
        }
    }
}

interface LogsToolbarCallbacks {
    fun onSearchButtonClicked(searchType: SearchType, text: String)
    fun updateToolbarFatalCheck(checked: Boolean)
    fun updateToolbarErrorCheck(checked: Boolean)
    fun updateToolbarWarningCheck(checked: Boolean)
    fun updateToolbarCommentsCheck(checked: Boolean)
    fun updateToolbarWrapContentCheck(checked: Boolean)
    fun onSearchUseRegexChanged(checked: Boolean)
    fun onColorFiltersClicked()
}

@Composable
fun LogsToolbar(
    state: LogsToolbarState,
    searchState: SearchState,
    searchAutoComplete: List<String>,
    callbacks: LogsToolbarCallbacks,
) {
    // Toolbar
    Row(verticalAlignment = Alignment.CenterVertically) {
        ToggleImageButton(
            checkedState = state.toolbarFatalChecked,
            icon = Res.drawable.icon_f,
            title = "Enable fatal logs highlight",
            checkedTintColor = Color.Red,
            updateCheckedState = callbacks::updateToolbarFatalCheck
        )
        ToggleImageButton(
            checkedState = state.toolbarErrorChecked,
            icon = Res.drawable.icon_e,
            title = "Enable error logs highlight",
            checkedTintColor = Color.Red,
            updateCheckedState = callbacks::updateToolbarErrorCheck
        )
        ToggleImageButton(
            checkedState = state.toolbarWarningChecked,
            icon = Res.drawable.icon_w,
            title = "Enable warning logs highlight",
            checkedTintColor = Color(0xE7, 0x62, 0x29),
            updateCheckedState = callbacks::updateToolbarWarningCheck
        )

        ToggleImageButton(
            checkedState = state.toolbarCommentsChecked,
            icon = Res.drawable.icon_comments,
            title = "Toggle comments",
            checkedTintColor = Color.Blue,
            updateCheckedState = callbacks::updateToolbarCommentsCheck,
        )

        ImageButton(
            modifier = Modifier.size(32.dp),
            icon = Res.drawable.icon_color_filters,
            title = "Color filters",
            onClick = callbacks::onColorFiltersClicked
        )

        HorizontalDivider(modifier = Modifier.height(32.dp))

        ImageButton(modifier = Modifier.size(32.dp),
            icon = if (searchState.state == SearchState.State.IDLE) {
                Res.drawable.icon_marked_logs
            } else {
                Res.drawable.icon_stop
            },
            title = "Show marked logs",
            onClick = {
                callbacks.onSearchButtonClicked(SearchType.MarkedRows, "")
            })


        var text by rememberSaveable { mutableStateOf(searchState.searchText) }
        ToggleImageButton(
            checkedState = searchState.searchUseRegex,
            icon = Res.drawable.icon_regex,
            title = "Use Regex or Plain text search",
            checkedTintColor = Color.Blue,
            updateCheckedState = callbacks::onSearchUseRegexChanged
        )

        AutoCompleteEditText(
            modifier = Modifier.height(20.dp).weight(1f)
            .onKeyEvent { e ->
                if (e.key == Key.Enter) {
                    callbacks.onSearchButtonClicked(SearchType.Text, text)
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
            icon = if (searchState.state == SearchState.State.IDLE) {
                Res.drawable.icon_search
            } else {
                Res.drawable.icon_stop
            },
            title = "Search",
            onClick = {
                callbacks.onSearchButtonClicked(SearchType.Text, text)
            })

        HorizontalDivider(modifier = Modifier.height(32.dp))

        ToggleImageButton(
            checkedState = state.toolbarWrapContentChecked,
            icon = Res.drawable.icon_wordwrap,
            title = "Wrap content",
            checkedTintColor = Color.Blue,
            updateCheckedState = callbacks::updateToolbarWrapContentCheck,
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
            toolbarCommentsChecked = false,
        ),
        searchState = SearchState(),
        searchAutoComplete = emptyList(),
        callbacks = object : LogsToolbarCallbacks {
            override fun onSearchButtonClicked(searchType: SearchType, text: String) {

            }

            override fun updateToolbarFatalCheck(checked: Boolean) {

            }

            override fun updateToolbarErrorCheck(checked: Boolean) {

            }

            override fun updateToolbarWarningCheck(checked: Boolean) {

            }

            override fun updateToolbarCommentsCheck(checked: Boolean) {

            }

            override fun updateToolbarWrapContentCheck(checked: Boolean) {

            }

            override fun onSearchUseRegexChanged(checked: Boolean) {

            }

            override fun onColorFiltersClicked() {

            }
        },
    )
}