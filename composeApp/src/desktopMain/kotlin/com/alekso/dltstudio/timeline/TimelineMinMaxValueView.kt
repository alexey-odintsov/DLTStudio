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
import androidx.compose.ui.unit.sp
import com.alekso.dltstudio.ParseSession
import com.alekso.dltstudio.colors.ColorPalette
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Locale


private const val SERIES_COUNT = 10

@Composable
fun TimelineMinMaxValueView(
    modifier: Modifier,
    entries: TimelineMinMaxEntries?,
    offsetSec: Float = 0f,
    scale: Float = 1f,
    dltSession: ParseSession,
    splitTimeSec: Float = 999f,
    seriesPostfix: String = "",
) {
    val textMeasurer = rememberTextMeasurer()

    Canvas(modifier = modifier.background(Color.Gray).clipToBounds()) {
        val height = size.height
        val width = size.width
        //val secSizePx: Float = (size.width / dltSession.totalSeconds) * scale
        val secSize: Float = width / (dltSession.totalSeconds * 1.dp.toPx())

        if (entries == null) return@Canvas
        entries.minValue = 0f
        val step = (entries.maxValue - entries.minValue) / SERIES_COUNT
        for (i in 0..SERIES_COUNT) {
            drawLine(
                Color.LightGray,
                Offset(0f, height * i / SERIES_COUNT),
                Offset(width, height * i / SERIES_COUNT),
                alpha = 0.5f
            )
            drawText(
                textMeasurer,
                text = "${"%.0f".format(entries.maxValue - (i * step))}$seriesPostfix",
                topLeft = Offset(3.dp.toPx(), height * i / SERIES_COUNT),
                style = TextStyle(color = Color.LightGray, fontSize = 12.sp)
            )
        }

        val map = entries.getEntriesMap()
        map.keys.forEachIndexed { index, key ->
            val items = map[key]
            items?.forEachIndexed entriesIteration@{ i, entry ->
                if (i == 0) return@entriesIteration
                val prev = if (i > 0) items[i - 1] else null

                val prevX = if (prev != null) {
                    val prevDiffSec = (entry.timestamp - prev.timestamp) / 1000f
                    // split lines if difference is too big
                    if (prevDiffSec > splitTimeSec) {
                        return@entriesIteration
                    } else (prev.timestamp - dltSession.timeStart) / 1000f * secSize.dp.toPx()
                } else {
                    0f
                }
                val curX = ((entry.timestamp - dltSession.timeStart) / 1000f * secSize.dp.toPx())


                val prevY =
                    if (prev != null) height - height * prev.value.toFloat() / entries.maxValue else 0f
                val curY = height - height * entry.value.toFloat() / entries.maxValue
                drawLine(
                    ColorPalette.getColor(index),
                    Offset(offsetSec * secSize.dp.toPx() * scale + prevX * scale, prevY),
                    Offset(offsetSec * secSize.dp.toPx() * scale + curX * scale, curY),
                    strokeWidth = 1.dp.toPx(),
                )
            }
        }
    }
}


@Preview
@Composable
fun PreviewTimelineMinMaxValueView() {
    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH)
    val dltSession = ParseSession({}, emptyList())
    val ts = Instant.now().toEpochMilli()

    dltSession.timeStart = ts
    dltSession.timeEnd = ts + 7000

    val entries = TimelineMinMaxEntries()
    entries.maxValue = 151f
    entries.minValue = 33f
    entries.entries["1325"] = mutableListOf(
        TimelineEntry(ts + 1450, "key1", "50"),
        TimelineEntry(ts + 2000, "key1", "83"),
        TimelineEntry(ts + 3300, "key1", "127"),
        TimelineEntry(ts + 4400, "key1", "89"),
    )
    entries.entries["435"] = mutableListOf(
        TimelineEntry(ts + 200, "435", "33"),
        TimelineEntry(ts + 2100, "435", "52"),
        TimelineEntry(ts + 2700, "435", "74"),
        TimelineEntry(ts + 3400, "435", "42"),
        TimelineEntry(ts + 3560, "435", "63"),
        TimelineEntry(ts + 4000, "435", "72"),
        TimelineEntry(ts + 6900, "435", "151"),
    )

    Column {
        Text(text = "start: ${simpleDateFormat.format(dltSession.timeStart)} (${dltSession.timeStart})")
        Text(text = "end: ${simpleDateFormat.format(dltSession.timeEnd)} (${dltSession.timeEnd})")
        Text(text = "seconds: ${dltSession.totalSeconds}")
        TimelineMinMaxValueView(
            modifier = Modifier.fillMaxSize(),
            entries = entries,
            offsetSec = 0f,
            dltSession = dltSession,
            scale = 1f,
            seriesPostfix = " Mb"
        )
    }
}