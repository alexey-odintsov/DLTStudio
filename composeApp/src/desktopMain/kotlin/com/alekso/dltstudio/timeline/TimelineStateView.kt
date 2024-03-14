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

@Composable
fun TimelineStateView(
    modifier: Modifier,
    entries: TimeLineStateEntries?,
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

        val itemHeight = height / entries.states.size.toFloat()
        val topOffset = itemHeight / 3f

        for (i in 0..<entries.states.size) {
            val y = i * itemHeight + topOffset
            drawLine(Color.LightGray, Offset(0f, y), Offset(width, y), alpha = 0.5f)
            drawText(
                textMeasurer,
                text = entries.states[i],
                topLeft = Offset(3.dp.toPx(), y),
                style = seriesTextStyle
            )
        }

        if (showVerticalSeries) {
            for (i in 0..timeFrame.getTotalSeconds()) {
                val x = timeFrame.offsetSeconds * secSizePx + i * secSizePx
                drawLine(Color.LightGray, Offset(x, 0f), Offset(x, height), alpha = 0.2f)
            }
        }

        val map = entries.map
        map.keys.forEachIndexed { index, key ->
            val items = map[key]
            renderLines(
                entries.states,
                items,
                splitTimeSec,
                timeFrame,
                secSizePx,
                height,
                ColorPalette.getColor(index, alpha = 0.5f),
                highlightedKey,
                key,
                topOffset,
                itemHeight
            )
        }

        if (highlightedKey != null) {
            val items = map[highlightedKey]
            renderLines(
                entries.states,
                items,
                splitTimeSec,
                timeFrame,
                secSizePx,
                height,
                Color.Green,
                highlightedKey,
                highlightedKey,
                topOffset,
                itemHeight
            )
        }
    }
}

private fun DrawScope.renderLines(
    states: List<String>,
    items: MutableList<TimeLineStateEntry>?,
    splitTimeSec: Float,
    timeFrame: TimeFrame,
    secSizePx: Float,
    height: Float,
    color: Color,
    highlightedKey: String?,
    key: String,
    topOffset: Float,
    itemHeight: Float
) {
    val regularStroke = 2.dp.toPx()
    val highlightedStroke = 3.dp.toPx()

    items?.forEachIndexed entriesIteration@{ i, entry ->

        val prev = if (i > 0) items[i - 1] else null

        val prevX = if (prev != null) {
            ((prev.timestamp - timeFrame.timestampStart) / 1000000f * secSizePx)
        } else {
            0f
        }
        val curX = ((entry.timestamp - timeFrame.timestampStart) / 1000000f * secSizePx)

        val curOldY = states.indexOf(entry.value.first) * itemHeight + topOffset
        val curY = states.indexOf(entry.value.second) * itemHeight + topOffset

        // horizontal line
        if (prev != null) {
            val prevY = states.indexOf(prev.value.second) * itemHeight + topOffset
            drawLine(
                color,
                Offset(timeFrame.offsetSeconds * secSizePx + prevX, prevY),
                Offset(timeFrame.offsetSeconds * secSizePx + curX, curOldY),
                strokeWidth = if (highlightedKey != null && highlightedKey == key) highlightedStroke else regularStroke
            )
        }
        // vertical line
        drawLine(
            color,
            Offset(timeFrame.offsetSeconds * secSizePx + curX, curOldY),
            Offset(timeFrame.offsetSeconds * secSizePx + curX, curY),
            strokeWidth = if (highlightedKey != null && highlightedKey == key) highlightedStroke else regularStroke
        )
    }
}

@Preview
@Composable
fun PreviewTimelineStateView() {
    val ts = Instant.now().toEpochMilli() * 1000L
    val te = ts + 7_000_000L

    val entries = TimeLineStateEntries()
    entries.map["10"] = mutableListOf()
    entries.addEntry(TimeLineStateEntry(ts + 1_450_000, "10", Pair("STATE_A", "STATE_B")))
    entries.addEntry(TimeLineStateEntry(ts + 2_000_000, "10", Pair("STATE_B", "STATE_C")))
    entries.addEntry(TimeLineStateEntry(ts + 4_000_000, "10", Pair("STATE_C", "STATE_A")))
    entries.addEntry(TimeLineStateEntry(ts + 6_000_000, "10", Pair("STATE_A", "STATE_D")))

    entries.map["0"] = mutableListOf()
    entries.addEntry(TimeLineStateEntry(ts + 550_000, "0", Pair("STATE_A", "STATE_B")))
    entries.addEntry(TimeLineStateEntry(ts + 3_023_000, "0", Pair("STATE_B", "STATE_D")))
    entries.addEntry(TimeLineStateEntry(ts + 6_200_000, "0", Pair("STATE_D", "STATE_C")))

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
            TimelineStateView(
                modifier = Modifier.fillMaxWidth().height(100.dp),
                entries = entries,
                timeFrame = timeFrame,
                showVerticalSeries = true,
                highlightedKey = "435"
            )
        }
    }
}