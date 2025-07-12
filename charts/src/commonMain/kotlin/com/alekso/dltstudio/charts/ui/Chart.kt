package com.alekso.dltstudio.charts.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.alekso.dltstudio.charts.model.ChartData
import com.alekso.dltstudio.charts.model.ChartEntry
import com.alekso.dltstudio.charts.model.ChartKey
import com.alekso.dltstudio.charts.model.DurationChartData
import com.alekso.dltstudio.charts.model.EventsChartData
import com.alekso.dltstudio.charts.model.MinMaxChartData
import com.alekso.dltstudio.charts.model.PercentageChartData
import com.alekso.dltstudio.charts.model.SingleStateChartData
import com.alekso.dltstudio.charts.model.StateChartData
import com.alekso.dltstudio.charts.model.TimeFrame
import kotlin.math.hypot

class PositionCache<T> {
    private val cache = mutableMapOf<Pair<ChartKey, Long>, Pair<Offset, ChartEntry<T>>>()

    fun put(key: ChartKey, timestamp: Long, offset: Offset, entry: ChartEntry<T>) {
        cache[Pair(key, timestamp)] = Pair(offset, entry)
    }

    fun get(key: ChartKey, timestamp: Long): Pair<Offset, ChartEntry<T>>? {
        return cache[Pair(key, timestamp)]
    }

    fun getNearestEntry(position: Offset): ChartEntry<T>? {
        val pixelThreshold = 15f
        val distances = mutableMapOf<Float, ChartEntry<T>>()
        for (key in cache.keys) {
            val entryPosition = cache[key] ?: continue
            val distance =
                hypot(position.x - entryPosition.first.x, position.y - entryPosition.first.y)
            if (distance <= pixelThreshold) {
                distances[distance] = entryPosition.second
            }
        }
        if (distances.isNotEmpty()) {
            val pair = distances.minBy { it.key }
            return pair.value
        }
        return null
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun <T> Chart(
    modifier: Modifier,
    style: ChartStyle = ChartStyle.Default,
    totalTime: TimeFrame, // min .. max timeStamps
    timeFrame: TimeFrame,
    entries: ChartData<T>?,
    onDragged: ((Float) -> Unit)? = null,
    type: ChartType,
    labelsCount: Int = 11,
    labelsPostfix: String = "",
    highlightedKey: ChartKey? = null,
    selectedEntry: ChartEntry<T>? = null,
    hoveredEntry: ChartEntry<T>? = null,
    onEntrySelected: ((ChartEntry<T>) -> Unit)? = null,
    onEntryHovered: ((ChartEntry<T>?) -> Unit)? = null,
) {
    var usSize by remember { mutableStateOf(1f) }
    val textMeasurer = rememberTextMeasurer()
    val positionCache = remember { PositionCache<T>() }
    var localCursorPosition by remember { mutableStateOf(Offset.Zero) }
    var isCursorInsideChart by remember { mutableStateOf(false) }
    if (type == ChartType.Events) {
        println("isCursorInsideChart: $isCursorInsideChart")
    }

    Box {
        Spacer(
            modifier = modifier.fillMaxSize().background(style.backgroundColor).clipToBounds()
                .onPointerEvent(PointerEventType.Enter) {
                    isCursorInsideChart = true
                }
                .onPointerEvent(PointerEventType.Exit) {
                    isCursorInsideChart = false
                }
            .onSizeChanged { size ->
                usSize = size.width.toFloat() / timeFrame.duration
            }.onPointerEvent(PointerEventType.Move) { event ->
                localCursorPosition = event.changes.first().position
                val hoveredEvent = positionCache.getNearestEntry(localCursorPosition)
                onEntryHovered?.invoke(hoveredEvent)
            }.pointerInput("chart-selection", entries, timeFrame) {
                detectTapGestures(
                    onPress = { offset ->
                        val selectedEvent = positionCache.getNearestEntry(offset)
                        if (selectedEvent != null) {
                            onEntrySelected?.invoke(selectedEvent)
                        }
                    })
            }.pointerInput("chart-dragging") {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    val dragUs = -dragAmount.x / usSize
                    onDragged?.invoke(dragUs)
                }
            }.drawWithCache {
                onDrawBehind {
                    renderCenterLine()
                    if (entries == null || entries.isEmpty()) {
                        renderEmptyMessage(textMeasurer, style)
                        return@onDrawBehind
                    }
                    val labelsSize = if (entries.getLabels()
                            .isNotEmpty()
                    ) entries.getLabels().size else labelsCount
                    renderSeries(type, labelsSize, style)
                    renderEntries(
                        type,
                        entries,
                        timeFrame,
                        style,
                        highlightedKey,
                        labelsSize,
                        selectedEntry,
                        hoveredEntry,
                        positionCache,
                    )
                    renderLabels(type, labelsSize, textMeasurer, style, entries, labelsPostfix)
                }
            })
        if (hoveredEntry != null && isCursorInsideChart) {
            val density = LocalDensity.current
            val xDp = with(density) { localCursorPosition.x.toDp() }
            val yDp = with(density) { localCursorPosition.y.toDp() }
            Text(
                hoveredEntry.getText(),
                Modifier.absoluteOffset(xDp, yDp - 20.dp).background(Color.White)
            )
        }
    }
}

private fun <T> DrawScope.renderLabels(
    type: ChartType,
    labelsSize: Int,
    textMeasurer: TextMeasurer,
    style: ChartStyle,
    entries: ChartData<T>,
    labelsPostfix: String
) {
    when (type) {
        ChartType.Percentage -> renderLabelsForValue(
            getSteps(0f, 100f, labelsSize),
            textMeasurer,
            style.labelTextStyle,
            "%",
            style.verticalPadding.toPx(),
        )

        ChartType.MinMax -> renderLabelsForValue(
            getSteps(
                (entries as MinMaxChartData).getMinValue(),
                (entries as MinMaxChartData).getMaxValue(),
                labelsSize
            ),
            textMeasurer,
            style.labelTextStyle,
            labelsPostfix,
            style.verticalPadding.toPx(),
        )

        ChartType.Events, ChartType.State, ChartType.SingleState, ChartType.Duration -> renderLabels(
            entries.getLabels(),
            textMeasurer,
            style.labelTextStyle,
            labelsPostfix,
            style.verticalPadding.toPx(),
        )
    }
}

private fun DrawScope.renderCenterLine() {
    drawLine(
        Color.LightGray, Offset(size.center.x, 0f), Offset(size.center.x, size.height), alpha = 0.5f
    )
}

private fun <T> DrawScope.renderEntries(
    type: ChartType,
    entries: ChartData<T>,
    timeFrame: TimeFrame,
    style: ChartStyle,
    highlightedKey: ChartKey?,
    labelsSize: Int,
    selectedEntry: ChartEntry<T>?,
    hoveredEntry: ChartEntry<T>?,
    positionCache: PositionCache<T>,
) {
    when (type) {
        ChartType.Events -> renderEvents(
            entries as EventsChartData,
            timeFrame,
            style,
            highlightedKey = highlightedKey,
            selectedEntry = selectedEntry,
            hoveredEntry = hoveredEntry,
            positionCache,
        )

        ChartType.Percentage -> renderPercentageLines(
            entries as PercentageChartData,
            labelsSize,
            timeFrame,
            style = style,
            highlightedKey = highlightedKey,
            selectedEntry = selectedEntry,
            hoveredEntry = hoveredEntry,
            positionCache,
        )

        ChartType.MinMax -> renderMinMaxLines(
            entries as MinMaxChartData,
            labelsSize,
            timeFrame,
            style = style,
            highlightedKey = highlightedKey,
            selectedEntry = selectedEntry,
            hoveredEntry = hoveredEntry,
            positionCache,
        )

        ChartType.State -> renderStateLines(
            entries as StateChartData,
            timeFrame,
            style = style,
            highlightedKey = highlightedKey,
            selectedEntry = selectedEntry,
            hoveredEntry = hoveredEntry,
            positionCache,
        )

        ChartType.SingleState -> renderSingleStateLines(
            entries as SingleStateChartData,
            timeFrame,
            style = style,
            highlightedKey = highlightedKey,
            selectedEntry = selectedEntry,
            hoveredEntry = hoveredEntry,
            positionCache,
        )

        ChartType.Duration -> renderDurationLines(
            entries as DurationChartData,
            timeFrame,
            style = style,
            highlightedKey = highlightedKey,
            selectedEntry = selectedEntry,
            hoveredEntry = hoveredEntry,
            positionCache,
        )
    }
}

private fun DrawScope.renderSeries(
    type: ChartType, labelsSize: Int, style: ChartStyle
) {
    when (type) {
        ChartType.Percentage, ChartType.MinMax -> renderSeriesByValue(
            labelsSize, style.seriesColor, style.verticalPadding.toPx()
        )

        else -> renderSeries(
            labelsSize, style.seriesColor, style.verticalPadding.toPx()
        )

    }
}

