package com.alekso.dltstudio.timeline.graph

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import com.alekso.dltstudio.LocalFormatter
import com.alekso.dltstudio.colors.ColorPalette
import com.alekso.dltstudio.timeline.TimeFrame
import com.alekso.dltstudio.timeline.TimeLineFloatEntry
import com.alekso.dltstudio.timeline.TimeLineMinMaxEntries
import com.alekso.dltstudio.timeline.TimeLineViewStyle
import kotlinx.datetime.Clock


private const val DEFAULT_SERIES_COUNT = 11


@Composable
fun TimelineMinMaxValueView(
    modifier: Modifier,
    viewStyle: TimeLineViewStyle = TimeLineViewStyle.Default,
    entries: TimeLineMinMaxEntries?,
    timeFrame: TimeFrame,
    splitTimeSec: Float = 999f,
    seriesPostfix: String = "",
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

    Spacer(modifier = modifier.background(Color.Gray).clipToBounds().drawWithCache {
        onDrawBehind {
            if (entries == null) return@onDrawBehind

            val height = size.height
            val width = size.width
            val verticalPaddingPx = verticalPaddingDp.toPx()
            val availableHeight = height - verticalPaddingPx * 2
            val secSizePx: Float = timeFrame.calculateSecSizePx(width)

            entries.minValue = 0f // always render 0 .. MAX

            renderVerticalSeries(
                seriesCount,
                availableHeight,
                verticalPaddingPx,
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
                    verticalPaddingPx,
                    entries.maxValue,
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
                    verticalPaddingPx,
                    entries.maxValue,
                    Color.Green,
                    highlightedKey,
                    highlightedKey
                )
            }

            renderLabels(
                entries.minValue,
                entries.maxValue,
                seriesCount,
                availableHeight,
                verticalPaddingPx,
                textMeasurer,
                seriesPostfix,
                seriesTextStyle
            )
        }
    })
}


@Preview
@Composable
fun PreviewTimelineMinMaxValueView() {
    val ts = Clock.System.now().toEpochMilliseconds() * 1000L
    val te = ts + 7_000_000L
    val key1 = "key1"
    val key2 = "key2"
    val key3 = "key3"

    val entries = TimeLineMinMaxEntries()
    entries.maxValue = 150f
    entries.minValue = 0f

    entries.map[key1] = mutableListOf(
        TimeLineFloatEntry(ts + 50_000, key1, 150f),
        TimeLineFloatEntry(ts + 550_000, key1, 149f),
        TimeLineFloatEntry(ts + 1_050_000, key1, 150f),
        TimeLineFloatEntry(ts + 1_450_000, key1, 110f),
        TimeLineFloatEntry(ts + 2_000_000, key1, 83f),
        TimeLineFloatEntry(ts + 3_300_000, key1, 127f),
        TimeLineFloatEntry(ts + 4_400_000, key1, 89f),
        TimeLineFloatEntry(ts + 4_500_000, key1, 0f),
        TimeLineFloatEntry(ts + 5_000_000, key1, 0f),
        TimeLineFloatEntry(ts + 6_000_000, key1, 0f),
    )
    entries.map[key2] = mutableListOf(
        TimeLineFloatEntry(ts + 200_000, key2, 133f),
        TimeLineFloatEntry(ts + 2_100_000, key2, 151f),
        TimeLineFloatEntry(ts + 2_700_000, key2, 104f),
        TimeLineFloatEntry(ts + 3_400_000, key2, 42f),
        TimeLineFloatEntry(ts + 3_560_000, key2, 63f),
        TimeLineFloatEntry(ts + 4_000_000, key2, 72f),
        TimeLineFloatEntry(ts + 6_800_000, key2, 111f),
    )
    entries.map[key3] = mutableListOf(
        TimeLineFloatEntry(ts + 2_300_000, key3, 100f),
    )

    Column {
        for (i in 1..3) {
            val timeFrame = TimeFrame(
                timestampStart = ts,
                timestampEnd = te,
                scale = i.toFloat(),
                offsetSeconds = 0f
            )

            Text(text = "start: ${LocalFormatter.current.formatDateTime(ts)}")
            Text(text = "end: ${LocalFormatter.current.formatDateTime(te)}")
            Text(text = "seconds: ${timeFrame.getTotalSeconds()}")
            TimelineMinMaxValueView(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                viewStyle = TimeLineViewStyle.Default,
                entries = entries,
                timeFrame = timeFrame,
                seriesPostfix = " Mb",
                highlightedKey = key2,
                showVerticalSeries = true,
                seriesCount = 10+i
            )
        }
    }
}