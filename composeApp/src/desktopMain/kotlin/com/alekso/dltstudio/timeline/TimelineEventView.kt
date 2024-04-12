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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import com.alekso.dltstudio.TimeFormatter
import com.alekso.dltstudio.colors.ColorPalette
import java.time.Instant

@Composable
fun TimelineEventView(
    modifier: Modifier,
    viewStyle: TimeLineViewStyle = TimeLineViewStyle.Default,
    entries: TimeLineEventEntries?,
    timeFrame: TimeFrame,
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
        val availableHeight = height - verticalPaddingDp.toPx() * 2
        val secSizePx: Float = timeFrame.calculateSecSizePx(width)
        val seriesCount = entries.states.size

        val itemHeight = availableHeight / (seriesCount - 1).toFloat()

        renderVerticalSeries(
            seriesCount - 1,
            availableHeight,
            verticalPaddingDp,
            width,
        )

        if (showVerticalSeries) {
            renderSecondsVerticalLines(timeFrame, secSizePx, height)
        }

        val map = entries.map
        map.keys.forEachIndexed { index, key ->
            val items = map[key]
            renderEvents(
                entries.states,
                items,
                timeFrame,
                secSizePx,
                verticalPaddingDp.toPx(),
                ColorPalette.getColor(index, alpha = 0.5f),
                highlightedKey,
                key,
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
                verticalPaddingDp.toPx(),
                Color.Green,
                highlightedKey,
                highlightedKey,
                itemHeight
            )
        }

        renderStateLabels(
            entries.states, seriesCount, itemHeight, verticalPaddingDp.toPx(),
            textMeasurer, seriesTextStyle
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
            val timeFrame = TimeFrame(
                timestampStart = ts,
                timestampEnd = te,
                scale = 1f,
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