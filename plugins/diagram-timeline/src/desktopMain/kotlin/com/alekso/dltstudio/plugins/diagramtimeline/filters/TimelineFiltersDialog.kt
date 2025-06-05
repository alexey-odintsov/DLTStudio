package com.alekso.dltstudio.plugins.diagramtimeline.filters

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.rememberDialogState
import com.alekso.dltstudio.plugins.diagramtimeline.DiagramType
import com.alekso.dltstudio.plugins.diagramtimeline.filters.edit.EditTimelineFilterDialog
import com.alekso.dltstudio.plugins.diagramtimeline.filters.edit.EditTimelineFilterDialogState
import com.alekso.dltstudio.plugins.diagramtimeline.filters.extractors.EntriesExtractor
import com.alekso.dltstudio.uicomponents.CustomButton
import com.alekso.dltstudio.uicomponents.CustomCheckbox
import com.alekso.dltstudio.uicomponents.ImageButton
import dltstudio.resources.Res
import dltstudio.resources.icon_delete
import dltstudio.resources.icon_down
import dltstudio.resources.icon_edit
import dltstudio.resources.icon_up


interface TimelineFiltersDialogCallbacks {
    fun onTimelineFilterUpdate(index: Int, filter: TimelineFilter)
    fun onTimelineFilterDelete(index: Int)
    fun onTimelineFilterMove(index: Int, offset: Int)
}

@Composable
fun TimelineFiltersDialog(
    visible: Boolean,
    onDialogClosed: () -> Unit,
    timelineFilters: List<TimelineFilter>,
    callbacks: TimelineFiltersDialogCallbacks,
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
                    callbacks.onTimelineFilterUpdate(i, filter)
                }
            )
        }

        ColorFiltersPanel(
            timelineFilters,
            { i, filter -> editDialogState.value = EditTimelineFilterDialogState(true, filter, i) },
            callbacks,
        )
    }
}

@Composable
fun ColorFiltersPanel(
    timelineFilters: List<TimelineFilter>,
    onEditFilterClick: (Int, TimelineFilter) -> Unit,
    callbacks: TimelineFiltersDialogCallbacks,
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
                        onClick = { callbacks.onTimelineFilterMove(i, -1) })

                    ImageButton(modifier = Modifier.size(28.dp),
                        icon = Res.drawable.icon_down,
                        title = "Move Down",
                        onClick = { callbacks.onTimelineFilterMove(i, 1) })

                    CustomCheckbox(
                        checked = filter.enabled,
                        modifier = Modifier.padding(horizontal = 8.dp),
                        onCheckedChange = {
                            callbacks.onTimelineFilterUpdate(
                                i,
                                filter.copy(enabled = !filter.enabled)
                            )
                        }
                    )

                    Box(
                        modifier = Modifier.weight(1f)
                            .padding(horizontal = 4.dp)
                    ) {
                        Text(
                            text = filter.name,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }

                    ImageButton(modifier = Modifier.size(28.dp),
                        icon = Res.drawable.icon_edit,
                        title = "Edit",
                        onClick = { onEditFilterClick(i, filter) })

                    ImageButton(modifier = Modifier.size(28.dp),
                        icon = Res.drawable.icon_delete,
                        title = "Delete",
                        onClick = { callbacks.onTimelineFilterDelete(i) })
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
            extractorType = EntriesExtractor.ExtractionType.NamedGroupsManyEntries
        )
    )

    val callbacks = object : TimelineFiltersDialogCallbacks {
        override fun onTimelineFilterUpdate(index: Int, filter: TimelineFilter) = Unit
        override fun onTimelineFilterDelete(index: Int) = Unit
        override fun onTimelineFilterMove(index: Int, offset: Int) = Unit
    }

    Column(Modifier.height(300.dp)) {
        ColorFiltersPanel(colorFilters, { _, _ -> }, callbacks)
    }
}