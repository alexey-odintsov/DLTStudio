package com.alekso.dltstudio.plugins.diagramtimeline

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alekso.dltstudio.charts.model.EventEntry
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
        val secSizePx: Float = (size.width / timeFrame.durationSec)

        if (debug) {
            drawText(
                textMeasurer,
                text = "${timeFrame.durationSec} seconds; Width: ${size.width}; Sec size: $secSizePx",
                topLeft = Offset(3.dp.toPx(), 0f),
                style = TextStyle(color = Color.LightGray, fontSize = 10.sp)
            )
        }

        for (i in 0..timeTotal.durationSec) {
            val lineTime = timeTotal.timeStart + 1_000_000 * i
            val lineEntry =
                EventEntry(lineTime, formatter.formatDateTime(lineTime), null)
            val x = calculateX(lineEntry, timeFrame, size.width)
            drawLine(
                Color.Gray,
                Offset(x, size.height / 2 + if (i % 10 == 0) 0 else 20),
                Offset(x, size.height + if (i % 10 == 0) 0 else 20),
            )
            if (i % 10 == 0) {
                val measuredText = textMeasurer.measure(
                    text = formatter.formatTime(lineEntry.timestamp),
                    style = TextStyle(color = Color.Gray, fontSize = 10.sp)
                )

                drawText(
                    measuredText,
                    topLeft = Offset(x, 30f),
                )
            }
        }
    }
}


@Preview
@Composable
fun PreviewTimeRuler() {
    val ts = Clock.System.now().toEpochMilliseconds() * 1000L
    val totalTime = TimeFrame(ts, ts + 100_000_000L)
    var timeFrame = TimeFrame(totalTime.timeStart, totalTime.timeEnd)
    val formatter = LocalFormatter.current

    Column {
        for (i in 1..5) {
            timeFrame = if (i == 1) timeFrame else timeFrame.zoom(true)

            Text(
                text = "Total: ${totalTime.durationSec}sec; ${formatter.formatDateTime(totalTime.timeStart)} - ${
                    formatter.formatDateTime(
                        totalTime.timeEnd
                    )
                }"
            )
            Text(
                text = "Frame: ${timeFrame.durationSec}sec; ${formatter.formatDateTime(timeFrame.timeStart)} - ${
                    formatter.formatDateTime(
                        timeFrame.timeEnd
                    )
                }"
            )
            Divider()

            TimeRuler(
                modifier = Modifier.fillMaxWidth(),
                timeTotal = totalTime,
                timeFrame = timeFrame,
            )
        }
    }
}