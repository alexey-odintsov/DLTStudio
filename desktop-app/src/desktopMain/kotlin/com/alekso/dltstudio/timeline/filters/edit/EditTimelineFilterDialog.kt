package com.alekso.dltstudio.timeline.filters.edit

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.rememberDialogState
import com.alekso.dltstudio.timeline.DiagramType
import com.alekso.dltstudio.timeline.filters.TimelineFilter
import com.alekso.dltstudio.timeline.filters.extractors.EntriesExtractor
import com.alekso.dltstudio.uicomponents.CustomButton
import com.alekso.dltstudio.uicomponents.CustomEditText
import com.alekso.dltstudio.uicomponents.TabsPanel


class EditTimelineFilterDialogState(
    var visible: Boolean,
    var filter: TimelineFilter = TimelineFilter.Empty,
    val filterIndex: Int = -1
)

@Composable
fun EditTimelineFilterDialog(
    visible: Boolean,
    onDialogClosed: () -> Unit,
    timelineFilter: TimelineFilter,
    onFilterUpdate: (Int, TimelineFilter) -> Unit,
    filterIndex: Int,
) {
    val dialogViewModel = remember {
        EditTimelineFilterViewModel(
            filterIndex,
            timelineFilter,
            onFilterUpdate,
            onDialogClosed
        )
    }
    DialogWindow(
        visible = visible, onCloseRequest = onDialogClosed,
        title = if (filterIndex >= 0) "Edit Timeline Filter" else "Add new Timeline filter",
        state = rememberDialogState(width = 700.dp, height = 600.dp)
    ) {
        EditTimelineFilterPanel(
            dialogViewModel,
            timelineFilter,
            filterIndex,
            onFilterUpdate,
            onDialogClosed,
        )
    }
}

private val colNameStyle = Modifier.padding(horizontal = 4.dp)


@Composable
fun EditTimelineFilterPanel(
    viewModel: EditTimelineFilterViewModel,
    filter: TimelineFilter,
    filterIndex: Int,
    onFilterUpdate: (Int, TimelineFilter) -> Unit,
    onDialogClosed: () -> Unit,
) {

    Column(
        Modifier.width(1000.dp).padding(4.dp),
        ) {
        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {


            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(modifier = colNameStyle, text = "Name")
                CustomEditText(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewModel.filterName, onValueChange = {
                        viewModel.filterName = it
                    }
                )
            }

            val tabs = remember { mutableStateListOf("Filtering", "Extraction") }
            TabsPanel(
                viewModel.tabIndex,
                tabs,
                { i -> viewModel.tabIndex = i })
            when (viewModel.tabIndex) {
                0 -> EditTimelineFilterFilterPanel(viewModel)

                1 -> EditTimelineFilterExtractPanel(viewModel)
            }
        }

        CustomButton(onClick = viewModel::onUpdateClicked) {
            Text(text = if (filterIndex >= 0) "Update" else "Add")
        }

    }
}

@Preview
@Composable
fun PreviewEditTimelineFilterDialog() {
    val filter = TimelineFilter(
        name = "CPU Usage by PID", enabled = true,
        filters = mutableMapOf(),
        extractPattern = """(cpu\d+?):\s?(\d+(?>.\d+)?)%\s?(cpu\d+?):\s?(\d+(?>.\d+)?)%\s?(cpu\d+?):\s?(\d+(?>.\d+)?)%\s?(cpu\d+?):\s?(\d+(?>.\d+)?)%\s?(cpu\d+?):\s?(\d+(?>.\d+)?)%\s?(cpu\d+?):\s?(\d+(?>.\d+)?)%\s?(cpu\d+?):\s?(\d+(?>.\d+)?)%\s?(cpu\d+?):\s?(\d+(?>.\d+)?)%\s?""",
        diagramType = DiagramType.Percentage,
        extractorType = EntriesExtractor.ExtractionType.NamedGroupsManyEntries,
        testClause = "cpu0: 36.9% cpu1: 40.4% cpu2: 40% cpu3: 43.5% cpu4: 45.3% cpu5: 27.9% cpu6: 16.8% cpu7: 14.1%",
    )

    Column(Modifier.background(Color(238, 238, 238))) {
        EditTimelineFilterPanel(
            EditTimelineFilterViewModel(0, filter, { i, f -> }, {}),
            filter,
            0,
            { _, _ -> },
            {},
        )
    }
}