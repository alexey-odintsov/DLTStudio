package com.alekso.dltstudio.plugins.diagramtimeline

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alekso.dltstudio.charts.model.TimeFrame
import com.alekso.dltstudio.charts.ui.calculateX
import kotlinx.datetime.Clock

@Composable
fun TimeRuler(
    modifier: Modifier = Modifier,
    timeTotal: TimeFrame,
    timeFrame: TimeFrame,
    debug: Boolean = false,
) {
    val textMeasurer = rememberTextMeasurer()
    val formatter = LocalFormatter.current
    Canvas(modifier = modifier.height(40.dp).clipToBounds()) {
        if (timeTotal.duration == 0L) return@Canvas

        val totalUs = timeTotal.duration
        val durationUs = timeFrame.duration
        val usSizePx: Double = (size.width.toDouble() / durationUs)
        val usPerPx = timeFrame.duration / size.width
        if (usSizePx >= Long.MAX_VALUE) return@Canvas

        val stepUs = calculateStepUs(usPerPx)
        if (debug) {
            drawText(
                textMeasurer,
                text = "total: ${formatDuration(totalUs)}; frame: ${formatDuration(durationUs)}; Width: ${size.width}; us: ${
                    "%.5f".format(
                        usSizePx
                    )
                }px;  us per width: ${"%.5f".format(usPerPx)} step: $stepUs",
                topLeft = Offset(3.dp.toPx(), 0f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(color = Color.Gray, fontSize = 10.sp)
            )
        }

        val offsetUs = timeFrame.timeStart - timeTotal.timeStart
        var currentUs = timeFrame.timeStart - offsetUs % stepUs
        var i = ((currentUs - timeTotal.timeStart) / stepUs).toInt()

        while (currentUs < timeFrame.timeEnd) {
            val x = calculateX(currentUs, timeFrame, size.width)

            drawLine(
                Color.Gray,
                Offset(x, size.height / 2 + if (i % 10 == 0) 0 else 20),
                Offset(x, size.height + if (i % 10 == 0) 0 else 20),
            )
            if (i % 10 == 0 && currentUs >= timeFrame.timeStart) {
                val measuredText = textMeasurer.measure(
                    text = formatter.formatTime(currentUs),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = TextStyle(color = Color.Gray, fontSize = 10.sp)
                )

                drawText(measuredText, topLeft = Offset(x, 30f))
            }
            currentUs += stepUs
            i++
        }
    }
}

private fun calculateStepUs(usPerPx: Float): Long {
    val minLabelSpacingPx = 150
    val labelMinUs = (minLabelSpacingPx * usPerPx).toLong()

    val baseSteps = listOf(
        1,
        2,
        5,
        10,
        20,
        50,
        100,
        200,
        500,
        1000,
        2000,
        5000,
        10000,
        20000,
        50000,
        100000,
        200000,
        500000,
        1000000,
        2000000,
        5000000,
    )
    var magnitude = 1L

    while (true) {
        for (base in baseSteps) {
            val stepUs = base * magnitude
            val labelSpacingUs = stepUs * 10
            if (labelSpacingUs >= labelMinUs) {
                return stepUs
            }
        }
        magnitude *= 10
    }
}

@Preview
@Composable
fun PreviewTimeRuler() {
    val ts = Clock.System.now().toEpochMilliseconds() * 1000L
    val frame1us = TimeFrame(ts, ts + 1L)
    val frame1ms = TimeFrame(ts, ts + 1_000L)
    val frame1sec = TimeFrame(ts, ts + 1_000_000L)
    val frame1min = TimeFrame(ts, ts + 60_000_000L)
    val frame10min = TimeFrame(ts, ts + 600_000_000L)
    val frame1hr = TimeFrame(ts, ts + 3_600_000_000L)
    val frame6hr = TimeFrame(ts, ts + 6 * 3_600_000_000L)
    val list = listOf(
        frame1us,
        frame1ms,
        frame1sec,
        frame1min,
        frame10min,
        frame1hr,
        frame6hr,
    )
    Column {
        list.forEach { frame ->
            Text("Duration: ${formatDuration(frame.duration)}")
            TimeRuler(
                modifier = Modifier.fillMaxWidth(),
                timeTotal = frame,
                timeFrame = frame,
                debug = true,
            )
            HorizontalDivider()
            val zoomed = frame.zoom(false)
            TimeRuler(
                modifier = Modifier.fillMaxWidth(),
                timeTotal = zoomed,
                timeFrame = zoomed,
                debug = true,
            )
            HorizontalDivider()
        }
    }
}

private fun formatDuration(durationUs: Long): String {
    val format = "%.1f"
    return when {
        durationUs >= 3_600_000_000 -> "${format.format(durationUs.toDouble() / 3_600_000_000)} hr"
        durationUs >= 60_000_000 -> "${format.format(durationUs.toDouble() / 60_000_000)} min"
        durationUs >= 1_000_000 -> "${format.format(durationUs.toDouble() / 1_000_000)} sec"
        durationUs >= 1_000 -> "${format.format(durationUs.toDouble() / 1_000)} ms"
        else -> "${durationUs}us"
    }
}