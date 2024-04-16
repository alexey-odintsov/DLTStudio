package com.alekso.dltstudio.timeline

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
        if (entries == null || entries.states.size < 1) return@Canvas

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
            renderEvents(
                entries.states,
                items,
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
            renderEvents(
                entries.states,
                items,
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
fun PreviewTimelineEventView() {
    val ts = Instant.now().toEpochMilli() * 1_000L
    val te = ts + 7_000_000L
    val key1 = "app1"
    val key2 = "app2"

    val timeFrame =
        TimeFrame(timestampStart = ts, timestampEnd = te, scale = 1f, offsetSeconds = 0f)

    val entriesEmpty = TimeLineEventEntries()
    val entries1Item = TimeLineEventEntries()
    val entries2Items = TimeLineEventEntries()
    val entries4Items = TimeLineEventEntries()
    val entriesManyItems = TimeLineEventEntries()

    val crash1 = TimeLineEventEntry(ts + 1_450_000, key1, TimeLineEvent("CRASH", "info 1"))
    val crash2 = TimeLineEventEntry(ts + 2_000_000, key2, TimeLineEvent("CRASH", "info 1"))
    val anr1 = TimeLineEventEntry(ts + 4_000_000, key1, TimeLineEvent("ANR", "info 1"))
    val lowMemory = TimeLineEventEntry(ts + 3_780_000, key1, TimeLineEvent("LOWMEMORY", "info 1"))
    val wtf = TimeLineEventEntry(ts + 4_380_000, key1, TimeLineEvent("WTF", "info 1"))
    val event1 = TimeLineEventEntry(ts + 5_380_000, key1, TimeLineEvent("EVENT 1", "info 1"))
    val event2 = TimeLineEventEntry(ts + 2_180_000, key1, TimeLineEvent("EVENT 2", "info 1"))
    val event3 = TimeLineEventEntry(ts + 1_380_000, key1, TimeLineEvent("EVENT 3", "info 1"))
    val event4 = TimeLineEventEntry(ts + 4_680_000, key1, TimeLineEvent("EVENT 4", "info 1"))
    val event5 = TimeLineEventEntry(ts + 1_050_000, key1, TimeLineEvent("EVENT 5", "info 1"))
    val event6 = TimeLineEventEntry(ts + 6_180_000, key1, TimeLineEvent("EVENT 6", "info 1"))
    val event7 = TimeLineEventEntry(ts + 5_380_000, key1, TimeLineEvent("EVENT 7", "info 1"))

    entriesManyItems.run {
        addEntry(crash1)
        addEntry(crash2)
        addEntry(anr1)
        addEntry(lowMemory)
        addEntry(wtf)
        addEntry(event1)
        addEntry(event2)
        addEntry(event3)
        addEntry(event4)
        addEntry(event5)
        addEntry(event6)
        addEntry(event7)
    }

    entries4Items.run {
        addEntry(crash1)
        addEntry(crash2)
        addEntry(anr1)
        addEntry(lowMemory)
        addEntry(wtf)
    }

    entries1Item.run {
        addEntry(crash1)
    }

    entries2Items.run {
        addEntry(crash1)
        addEntry(anr1)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "start: ${TimeFormatter.formatDateTime(ts)}")
        Text(text = "end: ${TimeFormatter.formatDateTime(te)}")
        Text(text = "seconds: ${timeFrame.getTotalSeconds()}")

        Text(modifier = Modifier.padding(top = 10.dp), text = "Empty")
        TimelineEventView(
            modifier = Modifier.fillMaxWidth().height(100.dp),
            entries = entriesEmpty,
            timeFrame = timeFrame,
            showVerticalSeries = true,
        )

        Text(modifier = Modifier.padding(top = 10.dp), text = "1 key")
        TimelineEventView(
            modifier = Modifier.fillMaxWidth().height(100.dp),
            entries = entries1Item,
            timeFrame = timeFrame,
            showVerticalSeries = true,
        )

        Text(modifier = Modifier.padding(top = 10.dp), text = "2 keys")
        TimelineEventView(
            modifier = Modifier.fillMaxWidth().height(100.dp),
            entries = entries2Items,
            timeFrame = timeFrame,
            showVerticalSeries = true,
        )

        Text(modifier = Modifier.padding(top = 10.dp), text = "4 keys")
        TimelineEventView(
            modifier = Modifier.fillMaxWidth().height(100.dp),
            entries = entries4Items,
            timeFrame = timeFrame,
            showVerticalSeries = true,
            highlightedKey = key2,
        )

        Text(modifier = Modifier.padding(top = 10.dp), text = "Many keys")
        TimelineEventView(
            modifier = Modifier.fillMaxWidth().height(100.dp),
            entries = entriesManyItems,
            timeFrame = timeFrame,
            showVerticalSeries = true,
        )
    }
}