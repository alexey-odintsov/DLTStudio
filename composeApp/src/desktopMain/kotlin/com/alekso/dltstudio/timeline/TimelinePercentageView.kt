package com.alekso.dltstudio.timeline

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import com.alekso.dltstudio.ParseSession
import com.alekso.dltstudio.colors.ColorPalette
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Locale

@Composable
fun TimelinePercentageView(
    modifier: Modifier,
    map: Map<String, List<TimelineEntry>>,
    offsetSec: Float = 0f,
    scale: Float = 1f,
    dltSession: ParseSession,
    splitTimeSec: Float = 5f,
) {
    val textMeasurer = rememberTextMeasurer()

    Canvas(modifier = modifier.background(Color.Gray).clipToBounds()) {
        val height = size.height
        val width = size.width
        val secSize: Float = width / (dltSession.totalSeconds * 1.dp.toPx())

        for (i in 0..100 step 10) {
            drawLine(
                Color.LightGray,
                Offset(0f, height * i / 100f),
                Offset(width, height * i / 100f),
                alpha = 0.5f
            )
            drawText(
                textMeasurer,
                text = "${100 - i}%",
                topLeft = Offset(3.dp.toPx(), height * i / 100f),
                style = TextStyle(color = Color.LightGray)
            )
        }


        map.keys.forEachIndexed { index, key ->
            val items = map[key]
            items?.forEachIndexed memEntriesIteration@{ i, entry ->
                if (i == 0) return@memEntriesIteration
                val prev = if (i > 0) items[i - 1] else null

                val prevX = if (prev != null) {
                    val prevDiffSec = (entry.timestamp - prev.timestamp) / 1000f
                    // split lines if difference is too big
                    if (prevDiffSec > splitTimeSec) {
                        return@memEntriesIteration
                    } else (prev.timestamp - dltSession.timeStart) / 1000f * secSize.dp.toPx()
                } else {
                    0f
                }
                val curX = ((entry.timestamp - dltSession.timeStart) / 1000f * secSize.dp.toPx())


                val prevY = if (prev != null) height - height * prev.value.toFloat() / 100f else 0f
                val curY = height - height * entry.value.toFloat() / 100f
                drawLine(
                    ColorPalette.getColor(index),
                    Offset(offsetSec * secSize.dp.toPx() * scale + prevX * scale, prevY),
                    Offset(offsetSec * secSize.dp.toPx() * scale + curX * scale, curY),
                )
            }
        }
    }
}

private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH)

@Preview
@Composable
fun PreviewTimelineView() {
    val dltSession = ParseSession({}, emptyList())
    val ts = Instant.now().toEpochMilli()

    dltSession.timeStart = ts
    dltSession.timeEnd = ts + 7000
    dltSession.totalSeconds = (dltSession.timeEnd - dltSession.timeStart).toInt() / 1000


    val map = mutableMapOf<String, List<TimelineEntry>>(
        "1325" to listOf(
            TimelineEntry(ts + 1450, "key1", "5.3"),
            TimelineEntry(ts + 2000, "key1", "43"),
            TimelineEntry(ts + 2300, "key1", "83"),
            TimelineEntry(ts + 2400, "key1", "43"),
        ),
        "435" to listOf(
            TimelineEntry(ts + 200, "435", "13"),
            TimelineEntry(ts + 2100, "435", "2"),
            TimelineEntry(ts + 2700, "435", "4"),
            TimelineEntry(ts + 3400, "435", "2"),
            TimelineEntry(ts + 3560, "435", "23"),
            TimelineEntry(ts + 4000, "435", "72"),
            TimelineEntry(ts + 6900, "435", "5"),
        ),
    )
    Column {
        Text(text = "start: ${simpleDateFormat.format(dltSession.timeStart)} (${dltSession.timeStart})")
        Text(text = "end: ${simpleDateFormat.format(dltSession.timeEnd)} (${dltSession.timeEnd})")
        Text(text = "seconds: ${dltSession.totalSeconds}")
        TimelinePercentageView(
            modifier = Modifier.fillMaxSize(),
            map = map,
            offsetSec = 0f,
            dltSession = dltSession,
            scale = 1f
        )
    }
}