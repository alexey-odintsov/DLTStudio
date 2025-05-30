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
import com.alekso.logger.Log
import kotlinx.datetime.Clock

private const val TIME_MARK_SIZE_PX = 100

@Composable
fun TimeRuler(
    modifier: Modifier = Modifier,
    offsetSec: Float,
    scale: Float,
    totalSeconds: Int,
    timeStart: Long,
    timeEnd: Long,
    debug: Boolean = false,
) {


    val textMeasurer = rememberTextMeasurer()
    val formatter = LocalFormatter.current
    Canvas(modifier = modifier.height(40.dp).clipToBounds()) {
        val secSizePx: Float = (size.width / totalSeconds) * scale
        val timeMarksCount = (size.width / TIME_MARK_SIZE_PX).toInt()

        if (debug) {
            drawText(
                textMeasurer,
                text = "$totalSeconds seconds; Width: ${size.width}; Sec size: $secSizePx",
                topLeft = Offset(3.dp.toPx(), 0f),
                style = TextStyle(color = Color.LightGray, fontSize = 10.sp)
            )
        }

        if (secSizePx < Float.POSITIVE_INFINITY && timeMarksCount > 0) {
            for (i in 0..totalSeconds step timeMarksCount) {
                try {
                    drawText(
                        textMeasurer,
                        text = formatter.formatTime(timeStart + i * 1000000),
                        topLeft = Offset(
                            offsetSec * secSizePx + i * secSizePx,
                            30f
                        ),
                        style = TextStyle(color = Color.Gray, fontSize = 10.sp)
                    )
                } catch (e: Exception) {
                    Log.e("Can't render $i x: ${offsetSec * secSizePx + i * secSizePx}")
                }
            }

        }

        // seconds lines
        for (i in 0..totalSeconds) {
            drawLine(
                Color.Gray,
                Offset(
                    offsetSec * secSizePx + i * secSizePx,
                    size.height / 2 + if (i % 10 == 0) 0 else 20
                ),
                Offset(
                    offsetSec * secSizePx + i * secSizePx,
                    size.height + if (i % 10 == 0) 0 else 20
                ),
            )
        }
    }
}


@Preview
@Composable
fun PreviewTimeRuler() {
    val ts = Clock.System.now().toEpochMilliseconds() * 1000L
    val te = ts + 7000000L
    val totalSeconds = (te - ts).toInt() / 1000000

    Column {
        Text(text = "start: ${LocalFormatter.current.formatDateTime(ts)} (${ts})")
        Text(text = "end: ${LocalFormatter.current.formatDateTime(te)} (${te})")
        Text(text = "seconds: $totalSeconds")
        Divider()

        for (i in 1..15) {

            TimeRuler(
                modifier = Modifier.fillMaxWidth(),
                offsetSec = 0f,
                scale = 2 * i.toFloat(),
                timeStart = ts,
                timeEnd = te,
                totalSeconds = totalSeconds,
            )
        }
    }
}