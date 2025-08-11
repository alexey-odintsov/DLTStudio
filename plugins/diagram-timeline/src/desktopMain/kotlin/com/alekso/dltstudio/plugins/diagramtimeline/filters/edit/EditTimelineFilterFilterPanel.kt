package com.alekso.dltstudio.plugins.diagramtimeline.filters.edit

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.alekso.dltstudio.plugins.diagramtimeline.DiagramType
import com.alekso.dltstudio.plugins.diagramtimeline.filters.TimelineFilter
import com.alekso.dltstudio.plugins.diagramtimeline.filters.extractors.EntriesExtractor
import com.alekso.dltstudio.theme.SystemTheme
import com.alekso.dltstudio.theme.ThemeManager
import com.alekso.dltstudio.uicomponents.CustomDropDown
import com.alekso.dltstudio.uicomponents.CustomEditText

private val COL_NAME_SIZE_DP = 150.dp
private val COL_VALUE = 250.dp
private val colNameStyle = Modifier.padding(horizontal = 4.dp)

@Composable
fun EditTimelineFilterFilterPanel(
    viewModel: EditTimelineFilterViewModel,
) {
    Row {
        Text(modifier = colNameStyle.width(COL_NAME_SIZE_DP), text = "Message Type")
        CustomDropDown(
            modifier = Modifier.width(COL_VALUE),
            items = viewModel.messageTypeItems,
            initialSelectedIndex = viewModel.messageTypeSelectionIndex,
            onItemsSelected = viewModel::onMessageTypeChanged
        )
    }

    Row {
        Text(modifier = colNameStyle.width(COL_NAME_SIZE_DP), text = "Message Type Info")
        CustomDropDown(
            modifier = Modifier.width(COL_VALUE),
            items = viewModel.messageTypeInfoItems,
            initialSelectedIndex = viewModel.messageTypeInfoSelectionIndex,
            onItemsSelected = viewModel::onMessageTypeInfoChanged
        )
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(modifier = colNameStyle.width(COL_NAME_SIZE_DP), text = "ECU ID")
        CustomEditText(
            modifier = Modifier.width(COL_VALUE),
            value = viewModel.ecuId ?: "", onValueChange = {
                viewModel.ecuId = it
            }
        )
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(modifier = colNameStyle.width(COL_NAME_SIZE_DP), text = "App ID")
        CustomEditText(
            modifier = Modifier.width(COL_VALUE),
            value = viewModel.appId ?: "", onValueChange = {
                viewModel.appId = it
            }
        )
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(modifier = colNameStyle.width(COL_NAME_SIZE_DP), text = "Context ID")
        CustomEditText(
            modifier = Modifier.width(COL_VALUE),
            value = viewModel.contextId ?: "", onValueChange = {
                viewModel.contextId = it
            }
        )
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(modifier = colNameStyle.width(COL_NAME_SIZE_DP), text = "Session ID")
        CustomEditText(
            modifier = Modifier.width(COL_VALUE),
            value = viewModel.sessionId ?: "", onValueChange = {
                viewModel.sessionId = it
            }
        )
    }
}


@Preview
@Composable
fun PreviewEditTimelineFilterFilterPanelThemes() {
    Column(modifier = Modifier.padding(4.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        ThemeManager.CustomTheme(SystemTheme(isDark = false)) {
            PreviewEditTimelineFilterFilterPanel()
        }
        ThemeManager.CustomTheme(SystemTheme(isDark = true)) {
            PreviewEditTimelineFilterFilterPanel()
        }
    }
}

@Preview
@Composable
fun PreviewEditTimelineFilterFilterPanel() {
    val filter = TimelineFilter(
        name = "CPU Usage by PID", enabled = true,
        filters = mutableMapOf(),
        extractPattern = """(cpu\d+?):\s?(\d+(?>.\d+)?)%\s?(cpu\d+?):\s?(\d+(?>.\d+)?)%\s?(cpu\d+?):\s?(\d+(?>.\d+)?)%\s?(cpu\d+?):\s?(\d+(?>.\d+)?)%\s?(cpu\d+?):\s?(\d+(?>.\d+)?)%\s?(cpu\d+?):\s?(\d+(?>.\d+)?)%\s?(cpu\d+?):\s?(\d+(?>.\d+)?)%\s?(cpu\d+?):\s?(\d+(?>.\d+)?)%\s?""",
        diagramType = DiagramType.Percentage,
        extractorType = EntriesExtractor.ExtractionType.NamedGroupsManyEntries,
        testClause = "cpu0: 36.9% cpu1: 40.4% cpu2: 40% cpu3: 43.5% cpu4: 45.3% cpu5: 27.9% cpu6: 16.8% cpu7: 14.1%",
    )

    Column(Modifier.background(Color(238, 238, 238))) {
        EditTimelineFilterFilterPanel(
            EditTimelineFilterViewModel(0, filter, {i, f ->}, {})
        )
    }
}