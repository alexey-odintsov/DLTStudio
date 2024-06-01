package com.alekso.dltstudio.timeline.graph

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import com.alekso.dltstudio.TimeFormatter
import com.alekso.dltstudio.colors.ColorPalette
import com.alekso.dltstudio.timeline.TimeFrame
import com.alekso.dltstudio.timeline.TimeLineStateEntries
import com.alekso.dltstudio.timeline.TimeLineStateEntry
import com.alekso.dltstudio.timeline.TimeLineViewStyle
import java.time.Instant

@Composable
fun TimelineStateView(
    modifier: Modifier,
    viewStyle: TimeLineViewStyle = TimeLineViewStyle.Default,
    entries: TimeLineStateEntries?,
    timeFrame: TimeFrame,
    splitTimeSec: Float = 999f,
    showVerticalSeries: Boolean = false,
    highlightedKey: String? = null,
) {
    val textMeasurer = rememberTextMeasurer()
    val verticalPaddingDp = viewStyle.verticalPaddingDp

    val seriesTextStyle = remember {
        TextStyle(
            color = viewStyle.fontColor,
            fontSize = viewStyle.fontSize,
            lineHeight = viewStyle.labelHeight,
            background = viewStyle.labelBackgroundColor,
            lineHeightStyle = LineHeightStyle(
                LineHeightStyle.Alignment.Center,
                LineHeightStyle.Trim.None
            )
        )
    }

    Canvas(modifier = modifier.background(Color.Gray).clipToBounds()) {
        if (entries == null) return@Canvas

        val height = size.height
        val width = size.width
        val verticalPaddingPx = verticalPaddingDp.toPx()
        val availableHeight = height - verticalPaddingPx * 2
        val secSizePx: Float = timeFrame.calculateSecSizePx(width)
        val seriesCount = entries.states.size

        renderVerticalSeries(
            seriesCount,
            availableHeight,
            verticalPaddingPx,
            width,
        )

        if (showVerticalSeries) {
            renderSecondsVerticalLines(timeFrame, secSizePx, height)
        }

        val map = entries.map
        map.keys.forEachIndexed { index, key ->
            val items = map[key]
            renderStateLines(
                entries.states,
                items,
                splitTimeSec,
                timeFrame,
                secSizePx,
                verticalPaddingPx,
                ColorPalette.getColor(index, alpha = 0.5f),
                highlightedKey,
                key,
                seriesCount,
                availableHeight,
            )
        }

        if (highlightedKey != null) {
            val items = map[highlightedKey]
            renderStateLines(
                entries.states,
                items,
                splitTimeSec,
                timeFrame,
                secSizePx,
                verticalPaddingPx,
                Color.Green,
                highlightedKey,
                highlightedKey,
                seriesCount,
                availableHeight,
            )
        }

        renderStateLabels(
            entries.states, seriesCount, verticalPaddingPx,
            textMeasurer, seriesTextStyle, availableHeight
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