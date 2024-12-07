package com.alekso.dltstudio.timeline.filters

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.rememberDialogState
import com.alekso.dltstudio.timeline.DiagramType
import com.alekso.dltstudio.timeline.filters.edit.EditTimelineFilterDialog
import com.alekso.dltstudio.timeline.filters.edit.EditTimelineFilterDialogState
import com.alekso.dltstudio.timeline.filters.extractors.EntriesExtractor
import com.alekso.dltstudio.ui.CustomButton
import com.alekso.dltstudio.ui.CustomCheckbox
import com.alekso.dltstudio.ui.ImageButton
import dtlstudio.composeapp.generated.resources.Res
import dtlstudio.composeapp.generated.resources.icon_delete
import dtlstudio.composeapp.generated.resources.icon_down
import dtlstudio.composeapp.generated.resources.icon_edit
import dtlstudio.composeapp.generated.resources.icon_up


@Composable
fun TimelineFiltersDialog(
    visible: Boolean,
    onDialogClosed: () -> Unit,
    timelineFilters: List<TimelineFilter>,
    onTimelineFilterUpdate: (Int, TimelineFilter) -> Unit,
    onTimelineFilterDelete: (Int) -> Unit,
    onTimelineFilterMove: (Int, Int) -> Unit,
) {
    DialogWindow(
        visible = visible, onCloseRequest = onDialogClosed,
        title = "Timeline Filters",
        state = rememberDialogState(width = 500.dp, height = 500.dp)
    ) {
        val editDialogState = remember { mutableStateOf(EditTimelineFilterDialogState(false)) }

        if (editDialogState.value.visible) {
            EditTimelineFilterDialog(
                visible = editDialogState.value.visible,
                onDialogClosed = { editDialogState.value = EditTimelineFilterDialogState(false) },
                timelineFilter = editDialogState.value.filter,
                filterIndex = editDialogState.value.filterIndex,
                onFilterUpdate = { i, filter ->
                    editDialogState.value.filter = filter
                    onTimelineFilterUpdate(i, filter)
                }
            )
        }

        ColorFiltersPanel(
            timelineFilters,
            { i, filter -> editDialogState.value = EditTimelineFilterDialogState(true, filter, i) },
            { i, f -> onTimelineFilterUpdate(i, f) },
            { i -> onTimelineFilterDelete(i) },
            { i, o -> onTimelineFilterMove(i, o) },
        )
    }
}

@Composable
fun ColorFiltersPanel(
    timelineFilters: List<TimelineFilter>,
    onEditFilterClick: (Int, TimelineFilter) -> Unit,
    onTimelineFilterUpdate: (Int, TimelineFilter) -> Unit,
    onTimelineFilterDelete: (Int) -> Unit,
    onTimelineFilterMove: (Int, Int) -> Unit,
) {

    Column(modifier = Modifier.padding(4.dp)) {
        LazyColumn(Modifier.weight(1f)) {
            items(timelineFilters.size) { i ->
                val filter = timelineFilters[i]
                Row(
                    Modifier.padding(horizontal = 4.dp, vertical = 0.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    ImageButton(modifier = Modifier.size(28.dp),
                        icon = Res.drawable.icon_up,
                        title = "Move Up",
                        onClick = { onTimelineFilterMove(i, -1) })

                    ImageButton(modifier = Modifier.size(28.dp),
                        icon = Res.drawable.icon_down,
                        title = "Move Down",
                        onClick = { onTimelineFilterMove(i, 1) })

                    var checked by remember { mutableStateOf(filter.enabled) }
                    CustomCheckbox(
                        checked = checked,
                        modifier = Modifier.padding(horizontal = 8.dp),
                        onCheckedChange = {
                            checked = !checked
                            onTimelineFilterUpdate(i, filter.copy(enabled = checked))
                        }
                    )

                    Box(
                        modifier = Modifier.weight(1f)
//                            .background(
//                                color = filter.cellStyle.backgroundColor ?: Color.Transparent
//                            )
                            .padding(horizontal = 4.dp)
                    ) {
                        Text(
                            text = filter.name,
                            modifier = Modifier.fillMaxWidth(),
//                            color = filter.cellStyle.textColor ?: Color.Black
                        )
                    }

                    ImageButton(modifier = Modifier.size(28.dp),
                        icon = Res.drawable.icon_edit,
                        title = "Edit",
                        onClick = { onEditFilterClick(i, filter) })

                    ImageButton(modifier = Modifier.size(28.dp),
                        icon = Res.drawable.icon_delete,
                        title = "Delete",
                        onClick = { onTimelineFilterDelete(i) })
                }
            }
        }

        CustomButton(
            onClick = {
                onEditFilterClick(-1, TimelineFilter.Empty)
            },
        ) {
            Text("Add filter")
        }
    }
}

@Preview
@Composable
fun PreviewTimelineFiltersDialog() {
    val colorFilters = mutableListOf(
        TimelineFilter(
            name = "CPU Usage by PID", enabled = true,
            filters = mutableMapOf(),
            extractPattern = "(?<value>\\d+.\\d+)\\s+%(?<key>(.*)pid\\s*:\\d+)\\(",
            diagramType = DiagramType.Percentage,
            extractorType = EntriesExtractor.ExtractionType.KeyValueNamed
        )
    )

    Column(Modifier.height(300.dp)) {
        ColorFiltersPanel(colorFilters, { i, f -> }, { i, f -> }, { i -> }, { i, o -> })
    }
}