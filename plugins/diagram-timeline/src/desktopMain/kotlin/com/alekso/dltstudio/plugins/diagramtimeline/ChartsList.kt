package com.alekso.dltstudio.plugins.diagramtimeline

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alekso.dltstudio.charts.model.ChartData
import com.alekso.dltstudio.charts.model.ChartEntry
import com.alekso.dltstudio.charts.model.ChartKey
import com.alekso.dltstudio.charts.model.TimeFrame
import com.alekso.dltstudio.charts.ui.Chart
import com.alekso.dltstudio.charts.ui.ChartType
import com.alekso.dltstudio.charts.ui.calculateTimestamp
import com.alekso.dltstudio.model.contract.LogMessage
import com.alekso.dltstudio.plugins.diagramtimeline.filters.TimelineFilter

private val TIME_MARKER_WIDTH_DP = 140.dp
private val TIME_MARKER_HEIGHT_DP = 12.dp

@Composable
@OptIn(ExperimentalComposeUiApi::class)
internal fun ChartsList(
    legendSize: Float,
    timeTotal: TimeFrame,
    timeFrame: TimeFrame,
    timelineFilters: SnapshotStateList<TimelineFilter>,
    entriesMap: SnapshotStateMap<String, ChartData<LogMessage>>,
    highlightedKeysMap: SnapshotStateMap<String, ChartKey?>,
    onLegendResized: (Float) -> Unit,
    retrieveEntriesForFilter: (filter: TimelineFilter) -> ChartData<LogMessage>?,
    toolbarCallbacks: ToolbarCallbacks,
    selectedEntry: ChartEntry<LogMessage>?,
    hoveredEntry: ChartEntry<LogMessage>?,
    onEntrySelected: ((ChartEntry<LogMessage>) -> Unit)?,
    onEntryHovered: ((ChartEntry<LogMessage>?) -> Unit)?,
    listState: LazyListState,
    modifier: Modifier
) {
    var cursorPosition by remember { mutableStateOf(Offset.Zero) }

    Column {
        Row {
            Box(modifier = Modifier.width(legendSize.dp))
            TimeRuler(
                Modifier.fillMaxWidth(1f),
                timeTotal = timeTotal,
                timeFrame = timeFrame,
            )
        }

        val panels = mutableStateListOf<@Composable () -> Unit>()
        val viewModifier = remember { Modifier.height(200.dp).weight(1f) }

        timelineFilters.forEachIndexed { index, timelineFilter ->
            if (timelineFilter.enabled) {
                panels.add {
                    Row {
                        TimelineLegend(
                            modifier = Modifier.width(legendSize.dp).height(200.dp),
                            title = timelineFilter.name,
                            entries = entriesMap[timelineFilter.key],
                            { key ->
                                highlightedKeysMap[timelineFilter.key] = key
                            },
                            highlightedKey = highlightedKeysMap[timelineFilter.key]
                        )
                        LegendResizer(
                            Modifier.height(200.dp),
                            key = "$index",
                            onResized = onLegendResized
                        )
                        val chartType = when (timelineFilter.diagramType) {
                            DiagramType.Percentage -> ChartType.Percentage
                            DiagramType.MinMaxValue -> ChartType.MinMax
                            DiagramType.State -> ChartType.State
                            DiagramType.SingleState -> ChartType.SingleState
                            DiagramType.Duration -> ChartType.Duration
                            DiagramType.Events -> ChartType.Events
                        }
                        Chart(
                            modifier = viewModifier,
                            entries = retrieveEntriesForFilter(timelineFilter),
                            highlightedKey = highlightedKeysMap[timelineFilter.key],
                            onDragged = toolbarCallbacks::onDragTimeline,
                            totalTime = timeTotal,
                            timeFrame = timeFrame,
                            type = chartType,
                            selectedEntry = selectedEntry,
                            hoveredEntry = hoveredEntry,
                            onEntrySelected = onEntrySelected,
                            onEntryHovered = onEntryHovered,
                        )
                    }
                }
            }
        }

        Box(modifier = Modifier.Companion.weight(1f)) {
            LazyColumn(
                Modifier.onPointerEvent(
                    eventType = PointerEventType.Move,
                    onEvent = { event ->
                        cursorPosition = event.changes[0].position
                    }),
                listState
            ) {
                items(panels.size) { i ->
                    if (i > 0) {
                        Divider(Modifier.padding(2.dp))
                    }
                    panels[i].invoke()
                }
            }
            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                adapter = rememberScrollbarAdapter(
                    scrollState = listState
                )
            )
            val textMeasurer = rememberTextMeasurer()
            val formatter = LocalFormatter.current
            Canvas(modifier = modifier.fillMaxSize().clipToBounds()) {
                if (cursorPosition.x < legendSize.dp.toPx()) return@Canvas

                val doesMarkerFit = (size.width - cursorPosition.x) > TIME_MARKER_WIDTH_DP.toPx()

                drawLine(
                    Color(0xff8080ff),
                    Offset(cursorPosition.x, 0f),
                    Offset(cursorPosition.x, size.height)
                )
                val cursorTimestamp = calculateTimestamp(
                    cursorPosition.x - legendSize.dp.toPx(),
                    timeFrame,
                    size.width - legendSize.dp.toPx()
                )
                drawText(
                    size = Size(TIME_MARKER_WIDTH_DP.toPx(), TIME_MARKER_HEIGHT_DP.toPx()),
                    textMeasurer = textMeasurer,
                    text = formatter.formatTime(cursorTimestamp),
                    topLeft = Offset(
                        if (doesMarkerFit) cursorPosition.x + 4.dp.toPx() else cursorPosition.x - TIME_MARKER_WIDTH_DP.toPx() - 4.dp.toPx(),
                        4.dp.toPx()
                    ),
                    style = TextStyle(
                        color = Color.Yellow,
                        fontSize = 10.sp,
                        background = Color(0x80808080),
                        textAlign = if (doesMarkerFit) TextAlign.Left else TextAlign.Right
                    ), overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}