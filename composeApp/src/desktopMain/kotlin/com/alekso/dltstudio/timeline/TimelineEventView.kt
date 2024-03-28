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
fun TimelineEventView(
    modifier: Modifier,
    entries: TimeLineEventEntries?,
    timeFrame: TimeFrame,
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
            renderEvents(
                entries.states,
                items,
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
            renderEvents(
                entries.states,
                items,
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

private const val EVENT_RADIUS_DP = 3

private fun DrawScope.renderEvents(
    states: List<String>,
    items: MutableList<TimeLineEventEntry>?,
    timeFrame: TimeFrame,
    secSizePx: Float,
    height: Float,
    color: Color,
    highlightedKey: String?,
    key: String,
    topOffset: Float,
    itemHeight: Float
) {
    items?.forEachIndexed entriesIteration@{ i, entry ->
        val curX = ((entry.timestamp - timeFrame.timestampStart) / 1000000f * secSizePx)
        val curY = states.indexOf(entry.value.event) * itemHeight + topOffset

        drawCircle(color, EVENT_RADIUS_DP.dp.toPx(),
            Offset(timeFrame.offsetSeconds * secSizePx + curX, curY),
        )
    }
}

@Preview
@Composable
fun PreviewTimelineEventView() {
    val ts = Instant.now().toEpochMilli() * 1000L
    val te = ts + 7_000_000L

    val entries = TimeLineEventEntries()
    entries.map["app1"] = mutableListOf()
    entries.map["app2"] = mutableListOf()
    entries.addEntry(TimeLineEventEntry(ts + 1_450_000, "app1", TimeLineEvent("CRASH", "info 1")))
    entries.addEntry(TimeLineEventEntry(ts + 2_000_000, "app2", TimeLineEvent("CRASH", "info 1")))
    entries.addEntry(TimeLineEventEntry(ts + 4_000_000, "app1", TimeLineEvent("ANR", "info 1")))

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
            TimelineEventView(
                modifier = Modifier.fillMaxWidth().height(100.dp),
                entries = entries,
                timeFrame = timeFrame,
                showVerticalSeries = true,
                highlightedKey = "435"
            )
        }
    }
}