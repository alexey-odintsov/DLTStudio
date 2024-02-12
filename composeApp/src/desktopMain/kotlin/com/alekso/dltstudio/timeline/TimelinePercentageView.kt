package com.alekso.dltstudio.timeline

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alekso.dltstudio.colors.ColorPalette
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Locale

@Composable
fun TimelinePercentageView(
    modifier: Modifier,
    entries: TimelinePercentageEntries?,
    timeFrame: TimeFrame,
    splitTimeSec: Float = 999f,
    showVerticalSeries: Boolean = false,
    highlightedKey: String? = null,
) {
    val textMeasurer = rememberTextMeasurer()
    val seriesTextStyle = remember { TextStyle(color = Color.LightGray, fontSize = 10.sp) }

    Canvas(modifier = modifier.background(Color.Gray).clipToBounds()) {
        val height = size.height
        val width = size.width
        val secSizePx: Float = timeFrame.calculateSecSizePx(width)

        if (entries == null) return@Canvas

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
                style = seriesTextStyle
            )
        }

        if (showVerticalSeries) {
            for (i in 0..timeFrame.getTotalSeconds()) {
                val x = timeFrame.offsetSeconds * secSizePx + i * secSizePx
                drawLine(Color.LightGray, Offset(x, 0f), Offset(x, height), alpha = 0.2f)
            }
        }

        val map = entries.getEntriesMap()
        map.keys.forEachIndexed { index, key ->
            val items = map[key]
            renderLines(
                items,
                splitTimeSec,
                timeFrame,
                secSizePx,
                height,
                ColorPalette.getColor(index),
                highlightedKey,
                key
            )
        }

        if (highlightedKey != null) {
            val items = map[highlightedKey]
            renderLines(
                items,
                splitTimeSec,
                timeFrame,
                secSizePx,
                height,
                Color.Green,
                highlightedKey,
                highlightedKey
            )
        }
    }
}

private fun DrawScope.renderLines(
    items: MutableList<TimelineEntry>?,
    splitTimeSec: Float,
    timeFrame: TimeFrame,
    secSizePx: Float,
    height: Float,
    color: Color,
    highlightedKey: String?,
    key: String
) {
    items?.forEachIndexed memEntriesIteration@{ i, entry ->
        if (i == 0) return@memEntriesIteration

        val prev = items[i - 1]
        val prevDiffSec = (entry.timestamp - prev.timestamp) / 1000f
        // split lines if difference is too big
        if (prevDiffSec > splitTimeSec) {
            return@memEntriesIteration
        }

        val prevX = (prev.timestamp - timeFrame.timestampStart) / 1000f * secSizePx
        val prevY = height - height * prev.value.toFloat() / 100f

        val curX = (entry.timestamp - timeFrame.timestampStart) / 1000f * secSizePx
        val curY = height - height * entry.value.toFloat() / 100f

        drawLine(
            color,
            Offset(timeFrame.offsetSeconds * secSizePx + prevX, prevY),
            Offset(timeFrame.offsetSeconds * secSizePx + curX, curY),
            strokeWidth = if (highlightedKey != null && highlightedKey == key) 2.dp.toPx() else 1f
        )
    }
}

@Preview
@Composable
fun PreviewTimelineView() {
    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH)
    val ts = Instant.now().toEpochMilli()
    val te = ts + 7000

    val entries = TimelinePercentageEntries()
    entries.entries["1325"] = mutableListOf(
        TimelineEntry(ts + 1450, "key1", "5.3"),
        TimelineEntry(ts + 2000, "key1", "43"),
        TimelineEntry(ts + 2300, "key1", "83"),
        TimelineEntry(ts + 2400, "key1", "43"),
    )
    entries.entries["435"] = mutableListOf(
        TimelineEntry(ts + 200, "435", "13"),
        TimelineEntry(ts + 2100, "435", "2"),
        TimelineEntry(ts + 2700, "435", "4"),
        TimelineEntry(ts + 3400, "435", "2"),
        TimelineEntry(ts + 3560, "435", "23"),
        TimelineEntry(ts + 4000, "435", "72"),
        TimelineEntry(ts + 6900, "435", "5"),
    )

    Column {
        for (i in 1..3) {
            val timeFrame = TimeFrame(
                timestampStart = ts,
                timestampEnd = te,
                scale = i.toFloat(),
                offsetSeconds = 0f
            )
            Text(text = "start: ${simpleDateFormat.format(ts)}")
            Text(text = "end: ${simpleDateFormat.format(te)}")
            Text(text = "seconds: ${timeFrame.getTotalSeconds()}")
            TimelinePercentageView(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                entries = entries,
                timeFrame = timeFrame,
                highlightedKey = "435"
            )
        }
    }
}