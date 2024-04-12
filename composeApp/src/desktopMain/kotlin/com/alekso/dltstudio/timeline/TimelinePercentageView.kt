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


private const val DEFAULT_SERIES_COUNT = 10

// TODO: Almost identical to TimelineMinMaxView - find a way to use one view
@Composable
fun TimelinePercentageView(
    modifier: Modifier,
    viewStyle: TimeLineViewStyle = TimeLineViewStyle.Default,
    entries: TimeLinePercentageEntries?,
    timeFrame: TimeFrame,
    splitTimeSec: Float = 999f,
    showVerticalSeries: Boolean = false,
    highlightedKey: String? = null,
    seriesCount: Int = DEFAULT_SERIES_COUNT
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

        renderVerticalSeries(
            seriesCount,
            availableHeight,
            verticalPaddingDp,
            width,
        )

        if (showVerticalSeries) {
            renderSecondsVerticalLines(timeFrame, secSizePx, height)
        }

        // Draw values
        val map = entries.map
        map.keys.forEachIndexed { index, key ->
            val items = map[key]
            renderLines(
                viewStyle,
                items,
                splitTimeSec,
                timeFrame,
                secSizePx,
                availableHeight,
                verticalPaddingDp.toPx(),
                100f,
                ColorPalette.getColor(index),
                highlightedKey,
                key
            )
        }

        if (highlightedKey != null) {
            val items = map[highlightedKey]
            renderLines(
                viewStyle,
                items,
                splitTimeSec,
                timeFrame,
                secSizePx,
                availableHeight,
                verticalPaddingDp.toPx(),
                100f,
                Color.Green,
                highlightedKey,
                highlightedKey
            )
        }

        renderLabels(
            0f,
            100f,
            seriesCount,
            availableHeight,
            verticalPaddingDp.toPx(),
            textMeasurer,
            "%",
            seriesTextStyle
        )
    }
}


@Preview
@Composable
fun PreviewTimelineView() {
    val ts = Instant.now().toEpochMilli() * 1000L
    val te = ts + 7_000_000L
    val key1 = "key1"
    val key2 = "key2"

    val entries = TimeLinePercentageEntries()

    entries.map[key1] = mutableListOf(
        TimeLineEntry(ts + 50_000, key1, 10f),
        TimeLineEntry(ts + 550_000, key1, 49f),
        TimeLineEntry(ts + 1_050_000, key1, 50f),
        TimeLineEntry(ts + 1_450_000, key1, 70f),
        TimeLineEntry(ts + 2_000_000, key1, 83f),
        TimeLineEntry(ts + 3_300_000, key1, 100f),
        TimeLineEntry(ts + 4_400_000, key1, 100f),
        TimeLineEntry(ts + 4_500_000, key1, 40f),
        TimeLineEntry(ts + 5_000_000, key1, 0f),
        TimeLineEntry(ts + 6_000_000, key1, 0f),
    )
    entries.map[key2] = mutableListOf(
        TimeLineEntry(ts + 200_000, key2, 0f),
        TimeLineEntry(ts + 2_100_000, key2, 0f),
        TimeLineEntry(ts + 2_700_000, key2, 4f),
        TimeLineEntry(ts + 3_400_000, key2, 42f),
        TimeLineEntry(ts + 3_560_000, key2, 63f),
        TimeLineEntry(ts + 4_000_000, key2, 72f),
        TimeLineEntry(ts + 6_800_000, key2, 100f),
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
            TimelinePercentageView(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                entries = entries,
                timeFrame = timeFrame,
                highlightedKey = key2,
            )
        }
    }
}