package com.alekso.dltstudio.charts.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.rememberTextMeasurer
import com.alekso.dltstudio.charts.model.ChartData
import com.alekso.dltstudio.charts.model.ChartKey
import com.alekso.dltstudio.charts.model.EventsChartData
import com.alekso.dltstudio.charts.model.MinMaxChartData
import com.alekso.dltstudio.charts.model.PercentageChartData
import com.alekso.dltstudio.charts.model.TimeFrame

@Composable
fun Chart(
    modifier: Modifier,
    style: ChartStyle,
    totalTime: TimeFrame, // min .. max timeStamps
    timeFrame: TimeFrame,
    entries: ChartData?,
    onDragged: (Float) -> Unit,
    type: ChartType,
    labelsCount: Int = 11,
    labelsPostfix: String = "",
    highlightedKey: ChartKey?,
) {
    if (entries == null || entries.isEmpty()) {
        Text("No entries found")
        return
    }
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
                    onDragged(dragUs)
                }
            }
            .drawWithCache {
                onDrawBehind {

                    // center line – zoom marker
                    drawLine(
                        Color.LightGray,
                        Offset(size.center.x, 0f),
                        Offset(size.center.x, size.height),
                        alpha = 0.5f
                    )

                    val labelsSize = if (entries.getLabels().isNotEmpty()) entries.getLabels().size else labelsCount
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

                    // draw entries
                    when (type) {
                        ChartType.Events -> renderEvents(
                            entries as EventsChartData,
                            labelsSize,
                            timeFrame,
                            style.verticalPadding.toPx()
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

                        else -> {}
                    }

                    when (type) {
                        ChartType.Percentage ->
                            renderLabels(
                                getSteps(0f, 100f, labelsSize),
                                textMeasurer,
                                style.labelTextStyle,
                                "%",
                                style.verticalPadding.toPx(),
                            )

                        ChartType.MinMax ->
                            renderLabels(
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
            }
    )
}
