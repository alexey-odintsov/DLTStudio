package com.alekso.dltstudio.charts.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.rememberTextMeasurer
import com.alekso.dltstudio.charts.model.ChartData
import com.alekso.dltstudio.charts.model.ChartKey
import com.alekso.dltstudio.charts.model.DurationChartData
import com.alekso.dltstudio.charts.model.EventsChartData
import com.alekso.dltstudio.charts.model.MinMaxChartData
import com.alekso.dltstudio.charts.model.PercentageChartData
import com.alekso.dltstudio.charts.model.SingleStateChartData
import com.alekso.dltstudio.charts.model.StateChartData
import com.alekso.dltstudio.charts.model.TimeFrame

@Composable
fun Chart(
    modifier: Modifier,
    style: ChartStyle = ChartStyle.Default,
    totalTime: TimeFrame, // min .. max timeStamps
    timeFrame: TimeFrame,
    entries: ChartData?,
    onDragged: ((Float) -> Unit)? = null,
    type: ChartType,
    labelsCount: Int = 11,
    labelsPostfix: String = "",
    highlightedKey: ChartKey? = null,
) {
    var usSize by remember { mutableStateOf(1f) }
    val textMeasurer = rememberTextMeasurer()

    Spacer(
        modifier = modifier.fillMaxSize().background(style.backgroundColor).clipToBounds()
            .onSizeChanged { size ->
                usSize = size.width.toFloat() / timeFrame.duration
            }
            .pointerInput("chart-dragging") {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    val dragUs = -dragAmount.x / usSize
                    onDragged?.invoke(dragUs)
                }
            }
            .drawWithCache {
                onDrawBehind {
                    renderCenterLine()
                    if (entries == null || entries.isEmpty()) {
                        renderEmptyMessage(textMeasurer, style)
                        return@onDrawBehind
                    }
                    val labelsSize = if (entries.getLabels().isNotEmpty()) entries.getLabels().size else labelsCount
                    renderSeries(type, labelsSize, style)
                    renderEntries(type, entries, timeFrame, style, highlightedKey, labelsSize)
                    renderLabels(type, labelsSize, textMeasurer, style, entries, labelsPostfix)
                }
            }
    )
}

private fun DrawScope.renderLabels(
    type: ChartType,
    labelsSize: Int,
    textMeasurer: TextMeasurer,
    style: ChartStyle,
    entries: ChartData,
    labelsPostfix: String
) {
    when (type) {
        ChartType.Percentage ->
            renderLabelsForValue(
                getSteps(0f, 100f, labelsSize),
                textMeasurer,
                style.labelTextStyle,
                "%",
                style.verticalPadding.toPx(),
            )

        ChartType.MinMax ->
            renderLabelsForValue(
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
        Color.LightGray,
        Offset(size.center.x, 0f),
        Offset(size.center.x, size.height),
        alpha = 0.5f
    )
}

private fun DrawScope.renderEntries(
    type: ChartType,
    entries: ChartData,
    timeFrame: TimeFrame,
    style: ChartStyle,
    highlightedKey: ChartKey?,
    labelsSize: Int
) {
    when (type) {
        ChartType.Events -> renderEvents(
            entries as EventsChartData,
            timeFrame,
            style,
            highlightedKey = highlightedKey,
        )

        ChartType.Percentage -> renderPercentageLines(
            entries as PercentageChartData,
            labelsSize,
            timeFrame,
            style = style,
            highlightedKey = highlightedKey,
        )

        ChartType.MinMax -> renderMinMaxLines(
            entries as MinMaxChartData,
            labelsSize,
            timeFrame,
            style = style,
            highlightedKey = highlightedKey,
        )

        ChartType.State -> renderStateLines(
            entries as StateChartData,
            timeFrame,
            style = style,
            highlightedKey = highlightedKey,
        )

        ChartType.SingleState -> renderSingleStateLines(
            entries as SingleStateChartData,
            timeFrame,
            style = style,
            highlightedKey = highlightedKey,
        )

        ChartType.Duration -> renderDurationLines(
            entries as DurationChartData,
            timeFrame,
            style = style,
            highlightedKey = highlightedKey,
        )
    }
}

private fun DrawScope.renderSeries(
    type: ChartType,
    labelsSize: Int,
    style: ChartStyle
) {
    when (type) {
        ChartType.Percentage, ChartType.MinMax ->
            renderSeriesByValue(
                labelsSize,
                style.seriesColor,
                style.verticalPadding.toPx()
            )

        else ->
            renderSeries(
                labelsSize,
                style.seriesColor,
                style.verticalPadding.toPx()
            )

    }
}

