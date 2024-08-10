package com.alekso.dltstudio.timeline.filters.edit

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.rememberDialogState
import com.alekso.dltstudio.logs.filtering.FilterCriteria
import com.alekso.dltstudio.logs.filtering.FilterParameter
import com.alekso.dltstudio.logs.filtering.TextCriteria
import com.alekso.dltstudio.timeline.DiagramType
import com.alekso.dltstudio.timeline.filters.TimelineFilter
import com.alekso.dltstudio.ui.CustomButton
import com.alekso.dltstudio.ui.CustomEditText
import com.alekso.dltstudio.ui.TabsPanel


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
    val dialogViewModel = remember { EditTimelineFilterViewModel(timelineFilter) }
    DialogWindow(
        visible = visible, onCloseRequest = onDialogClosed,
        title = if (filterIndex >= 0) "Edit Timeline Filter" else "Add new Timeline filter",
        state = rememberDialogState(width = 700.dp, height = 500.dp)
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

private val COL_NAME_SIZE_DP = 150.dp
private val COL_VALUE = 250.dp
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
            modifier = Modifier.weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {


            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(modifier = colNameStyle, text = "Name")
                CustomEditText(
                    modifier = Modifier.width(COL_VALUE * 2),
                    value = viewModel.filterName, onValueChange = {
                        viewModel.filterName = it
                    }
                )
            }

            TabsPanel(0, listOf("Filtering", "Extraction"), { i -> viewModel.tabIndex = i })
            when (viewModel.tabIndex) {
                0 -> EditTimelineFilterFilterPanel(viewModel)

                1 -> EditTimelineFilterExtractPanel(viewModel)
            }
        }

        CustomButton(onClick = {
            val map = mutableMapOf<FilterParameter, FilterCriteria>()
            viewModel.messageType?.let {
                map[FilterParameter.MessageType] = FilterCriteria(it, TextCriteria.PlainText)
            }
            viewModel.messageTypeInfo?.let {
                map[FilterParameter.MessageTypeInfo] = FilterCriteria(it, TextCriteria.PlainText)
            }
            viewModel.ecuId?.let {
                map[FilterParameter.EcuId] = FilterCriteria(it, TextCriteria.PlainText)
            }
            viewModel.appId?.let {
                map[FilterParameter.AppId] = FilterCriteria(it, TextCriteria.PlainText)
            }
            viewModel.contextId?.let {
                map[FilterParameter.ContextId] = FilterCriteria(it, TextCriteria.PlainText)
            }
            viewModel.sessionId?.let {
                map[FilterParameter.SessionId] = FilterCriteria(it, TextCriteria.PlainText)
            }
            onFilterUpdate(
                filterIndex,
                TimelineFilter(
                    name = viewModel.filterName,
                    filters = map,
                    extractPattern = viewModel.extractPattern,
                    diagramType = DiagramType.valueOf(viewModel.diagramType),
                    extractorType = TimelineFilter.ExtractorType.valueOf(viewModel.extractorType),
                    testClause = viewModel.testPayload,
                )
            )
            onDialogClosed()
        }) {
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
        extractorType = TimelineFilter.ExtractorType.KeyValueNamed,
        testClause = "cpu0: 36.9% cpu1: 40.4% cpu2: 40% cpu3: 43.5% cpu4: 45.3% cpu5: 27.9% cpu6: 16.8% cpu7: 14.1%",
    )

    Column(Modifier.background(Color(238, 238, 238))) {
        EditTimelineFilterPanel(
            EditTimelineFilterViewModel(filter),
            filter,
            0,
            { _, _ -> },
            {},
        )
    }
}