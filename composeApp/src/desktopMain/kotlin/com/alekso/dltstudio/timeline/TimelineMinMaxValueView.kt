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
import com.alekso.dltstudio.TimeFormatter
import com.alekso.dltstudio.colors.ColorPalette
import java.time.Instant


private const val SERIES_COUNT = 10

@Composable
fun TimelineMinMaxValueView(
    modifier: Modifier,
    entries: TimeLineMinMaxEntries?,
    timeFrame: TimeFrame,
    splitTimeSec: Float = 999f,
    seriesPostfix: String = "",
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
        entries.minValue = 0f

        // Draw series lines
        val step = (entries.maxValue - entries.minValue) / SERIES_COUNT
        for (i in 0..SERIES_COUNT) {
            val y = height * i / SERIES_COUNT
            drawLine(Color.LightGray, Offset(0f, y), Offset(width, y), alpha = 0.5f)
            drawText(
                textMeasurer,
                text = "${"%.0f".format(entries.maxValue - (i * step))}$seriesPostfix",
                topLeft = Offset(3.dp.toPx(), height * i / SERIES_COUNT),
                style = seriesTextStyle
            )
        }

        if (showVerticalSeries) {
            for (i in 0..timeFrame.getTotalSeconds()) {
                val x = timeFrame.offsetSeconds * secSizePx + i * secSizePx
                drawLine(Color.LightGray, Offset(x, 0f), Offset(x, height), alpha = 0.2f)
            }
        }

        // Draw values
        val map = entries.map
        map.keys.forEachIndexed { index, key ->
            val items = map[key]
            renderLines(
                items,
                splitTimeSec,
                timeFrame,
                secSizePx,
                height,
                entries,
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
                entries,
                Color.Green,
                highlightedKey,
                highlightedKey
            )
        }
    }
}

private fun DrawScope.renderLines(
    items: MutableList<TimeLineEntry<Float>>?,
    splitTimeSec: Float,
    timeFrame: TimeFrame,
    secSizePx: Float,
    height: Float,
    entries: TimeLineMinMaxEntries,
    color: Color,
    highlightedKey: String?,
    key: String
) {
    items?.forEachIndexed entriesIteration@{ i, entry ->
        if (i == 0) return@entriesIteration

        val prev = items[i - 1]
        val prevDiffSec = (entry.timestamp - prev.timestamp) / 1000000f
        // split lines if difference is too big
        if (prevDiffSec > splitTimeSec) {
            return@entriesIteration
        }
        val prevX = (prev.timestamp - timeFrame.timestampStart) / 1000000f * secSizePx
        val prevY = height - height * prev.value.toFloat() / entries.maxValue

        val curX = ((entry.timestamp - timeFrame.timestampStart) / 1000000f * secSizePx)
        val curY = height - height * entry.value.toFloat() / entries.maxValue

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
fun PreviewTimelineMinMaxValueView() {
    val ts = Instant.now().toEpochMilli() * 1000L
    val te = ts + 7000000L

    val entries = TimeLineMinMaxEntries()
    entries.maxValue = 151f
    entries.minValue = 33f
    entries.map["1325"] = mutableListOf(
        TimeLineEntry<Float>(ts + 1450000, "1325", 110f),
        TimeLineEntry<Float>(ts + 2000000, "1325", 83f),
        TimeLineEntry<Float>(ts + 3300000, "1325", 127f),
        TimeLineEntry<Float>(ts + 4400000, "1325", 89f),
    )
    entries.map["435"] = mutableListOf(
        TimeLineEntry<Float>(ts + 200000, "435", 133f),
        TimeLineEntry<Float>(ts + 2100000, "435", 151f),
        TimeLineEntry<Float>(ts + 2700000, "435", 104f),
        TimeLineEntry<Float>(ts + 3400000, "435", 42f),
        TimeLineEntry<Float>(ts + 3560000, "435", 63f),
        TimeLineEntry<Float>(ts + 4000000, "435", 72f),
        TimeLineEntry<Float>(ts + 6800000, "435", 111f),
    )

    Column {
        for (i in 1..3) {
            val timeFrame = TimeFrame(
                timestampStart = ts,
                timestampEnd = te,
                scale = i.toFloat(),
                offsetSeconds = 0f
            )

            Text(text = "start: ${TimeFormatter.formatDateTime(ts)}")
            Text(text = "end: ${TimeFormatter.formatDateTime(te)}")
            Text(text = "seconds: ${timeFrame.getTotalSeconds()}")
            TimelineMinMaxValueView(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                entries = entries,
                timeFrame = timeFrame,
                seriesPostfix = " Mb",
                highlightedKey = "1325"
            )
        }
    }
}