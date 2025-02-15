package com.alekso.dltstudio.logs.toolbar

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.unit.dp
import com.alekso.dltstudio.TimeFormatter
import com.alekso.dltstudio.logs.search.SearchState
import com.alekso.dltstudio.logs.search.SearchType
import com.alekso.dltstudio.ui.ImageButton
import com.alekso.dltstudio.ui.ToggleImageButton
import com.alekso.dltstudio.uicomponents.AutoCompleteEditText
import com.alekso.dltstudio.uicomponents.HorizontalDivider
import com.alekso.dltstudio.uicomponents.Tooltip
import dltstudio.composeapp.generated.resources.Res
import dltstudio.composeapp.generated.resources.icon_color_filters
import dltstudio.composeapp.generated.resources.icon_comments
import dltstudio.composeapp.generated.resources.icon_e
import dltstudio.composeapp.generated.resources.icon_f
import dltstudio.composeapp.generated.resources.icon_marked_logs
import dltstudio.composeapp.generated.resources.icon_regex
import dltstudio.composeapp.generated.resources.icon_search
import dltstudio.composeapp.generated.resources.icon_search_marks
import dltstudio.composeapp.generated.resources.icon_stop
import dltstudio.composeapp.generated.resources.icon_w
import dltstudio.composeapp.generated.resources.icon_wordwrap
import kotlinx.datetime.TimeZone


@Composable
fun LogsToolbar(
    state: LogsToolbarState,
    searchState: SearchState,
    searchAutoComplete: SnapshotStateList<String>,
    callbacks: LogsToolbarCallbacks,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Tooltip(text = "Toggle fatal logs highlight") {
            ToggleImageButton(
                checkedState = state.toolbarFatalChecked,
                icon = Res.drawable.icon_f,
                title = "Enable fatal logs highlight",
                checkedTintColor = Color.Red,
                updateCheckedState = callbacks::updateToolbarFatalCheck
            )
        }
        Tooltip(text = "Toggle error logs highlight") {
            ToggleImageButton(
                checkedState = state.toolbarErrorChecked,
                icon = Res.drawable.icon_e,
                title = "Enable error logs highlight",
                checkedTintColor = Color.Red,
                updateCheckedState = callbacks::updateToolbarErrorCheck
            )
        }
        Tooltip(text = "Toggle warning logs highlight") {
            ToggleImageButton(
                checkedState = state.toolbarWarningChecked,
                icon = Res.drawable.icon_w,
                title = "Enable warning logs highlight",
                checkedTintColor = Color(0xE7, 0x62, 0x29),
                updateCheckedState = callbacks::updateToolbarWarningCheck
            )
        }
        Tooltip(text = "Toggle comments") {
            ToggleImageButton(
                checkedState = state.toolbarCommentsChecked,
                icon = Res.drawable.icon_comments,
                title = "Toggle comments",
                checkedTintColor = Color.Blue,
                updateCheckedState = callbacks::updateToolbarCommentsCheck,
            )
        }
        Tooltip(text = "Toggle content wrapping") {
            ToggleImageButton(
                checkedState = state.toolbarWrapContentChecked,
                icon = Res.drawable.icon_wordwrap,
                title = "Wrap content",
                checkedTintColor = Color.Blue,
                updateCheckedState = callbacks::updateToolbarWrapContentCheck,
            )
        }
        Tooltip(text = "Manage color filters") {
            ImageButton(
                modifier = Modifier.size(32.dp),
                icon = Res.drawable.icon_color_filters,
                title = "Color filters",
                onClick = callbacks::onColorFiltersClicked
            )
        }
        HorizontalDivider(modifier = Modifier.height(32.dp))

        Tooltip(text = "Show marked logs") {
            ImageButton(
                modifier = Modifier.size(32.dp),
                icon = if (searchState.state == SearchState.State.IDLE) {
                    Res.drawable.icon_marked_logs
                } else {
                    Res.drawable.icon_stop
                },
                title = "Show marked logs",
                onClick = {
                    callbacks.onSearchButtonClicked(SearchType.MarkedRows, "")
                }
            )
        }
        Tooltip(text = "Show marked logs along with search results") {
            ToggleImageButton(
                checkedState = state.toolbarSearchWithMarkedChecked,
                icon = Res.drawable.icon_search_marks,
                title = "Search results with marked logs",
                checkedTintColor = Color.Blue,
                updateCheckedState = callbacks::updateToolbarSearchWithMarkedCheck,
            )
        }
        var text by rememberSaveable { mutableStateOf(searchState.searchText) }
        Tooltip(text = "Toggle regular expression or plain search") {
            ToggleImageButton(
                checkedState = searchState.searchUseRegex,
                icon = Res.drawable.icon_regex,
                title = "Use Regex or Plain text search",
                checkedTintColor = Color.Blue,
                updateCheckedState = callbacks::onSearchUseRegexChanged
            )
        }
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

        Tooltip(text = "Start/Stop search") {
            ImageButton(
                modifier = Modifier.size(32.dp),
                icon = if (searchState.state == SearchState.State.IDLE) {
                    Res.drawable.icon_search
                } else {
                    Res.drawable.icon_stop
                },
                title = "Search",
                onClick = {
                    callbacks.onSearchButtonClicked(SearchType.Text, text)
                })
        }
        HorizontalDivider(modifier = Modifier.height(32.dp))

        var timeZoneText by rememberSaveable { mutableStateOf(TimeFormatter.timeZone.toString()) }
        AutoCompleteEditText(
            modifier = Modifier.width(150.dp).padding(end = 4.dp),
            value = timeZoneText,
            onValueChange = {
                timeZoneText = it
                callbacks.onTimeZoneChanged(it)
            },
            items = TimeZone.availableZoneIds.map { it }.toMutableStateList()
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
            toolbarSearchWithMarkedChecked = false,
            toolbarWrapContentChecked = true,
            toolbarCommentsChecked = false,
        ),
        searchState = SearchState(),
        searchAutoComplete = mutableStateListOf(),
        callbacks = LogsToolbarCallbacks.Stub,
    )
}
