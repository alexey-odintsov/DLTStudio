package com.alekso.dltstudio.timeline

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
import com.alekso.dltstudio.ParseSession
import java.text.SimpleDateFormat
import java.util.Locale

private val TimeRulerTimeFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.ENGLISH)
private const val TIME_MARK_SIZE_PX = 100
private const val DEBUG = true

@Composable
fun TimeRuler(
    modifier: Modifier = Modifier,
    offsetSec: Float,
    scale: Float,
    dltSession: ParseSession,
) {


    val textMeasurer = rememberTextMeasurer()

    Canvas(modifier = modifier.height(40.dp).clipToBounds()) {
        val secSizePx: Float = (size.width / dltSession.totalSeconds) * scale
        val timeMarksCount = (size.width / TIME_MARK_SIZE_PX).toInt()

        if (DEBUG) {
            drawText(
                textMeasurer,
                text = "${dltSession.totalSeconds} seconds; Width: ${size.width}; Sec size: $secSizePx",
                topLeft = Offset(3.dp.toPx(), 0f),
                style = TextStyle(color = Color.LightGray, fontSize = 10.sp)
            )
        }

        if (secSizePx < Float.POSITIVE_INFINITY) {
            for (i in 0..dltSession.totalSeconds step timeMarksCount) {
                try {
                    drawText(
                        textMeasurer,
                        text = TimeRulerTimeFormat.format(dltSession.timeStart + i * 1000),
                        topLeft = Offset(
                            offsetSec * secSizePx + i * secSizePx,
                            30f
                        ),
                        style = TextStyle(color = Color.Gray, fontSize = 10.sp)
                    )
                } catch (e: Exception) {
                    println("Can't render $i x: ${offsetSec * secSizePx + i * secSizePx}")
                }
            }

        }

        // seconds lines
        for (i in 0..dltSession.totalSeconds) {
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

private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH)

@Preview
@Composable
fun PreviewTimeRuler() {
    val dltSession = ParseSession({}, emptyList())
    val ts = 1707602400000 //23:00:00.000 // Instant.now().toEpochMilli()

    dltSession.timeStart = ts
    dltSession.timeEnd = ts + 100_000

    Column {
        Text(text = "start: ${simpleDateFormat.format(dltSession.timeStart)} (${dltSession.timeStart})")
        Text(text = "end: ${simpleDateFormat.format(dltSession.timeEnd)} (${dltSession.timeEnd})")
        Text(text = "seconds: ${dltSession.totalSeconds}")
        Divider()

        for (i in 1..15) {

            TimeRuler(
                modifier = Modifier.fillMaxWidth(),
                offsetSec = 0f,
                scale = 2 * i.toFloat(),
                dltSession = dltSession
            )
        }
    }
}