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
import com.alekso.dltstudio.TimeFormatter
import com.alekso.dltstudio.colors.ColorPalette
import com.alekso.dltstudio.timeline.TimeFrame
import com.alekso.dltstudio.timeline.TimeLineSingleStateEntries
import com.alekso.dltstudio.timeline.TimeLineSingleStateEntry
import com.alekso.dltstudio.timeline.TimeLineViewStyle
import kotlinx.datetime.Clock

@Composable
fun TimelineSingleStateView(
    modifier: Modifier,
    viewStyle: TimeLineViewStyle = TimeLineViewStyle.Default,
    entries: TimeLineSingleStateEntries?,
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

    Spacer(modifier = modifier.background(Color.Gray).clipToBounds().drawWithCache {
        onDrawBehind {
            if (entries == null) return@onDrawBehind

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
                renderSingleStateLines(
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
                renderSingleStateLines(
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
    })
}



@Preview
@Composable
fun PreviewTimelineSingleStateView() {
    val ts = Clock.System.now().toEpochMilliseconds() * 1000L
    val te = ts + 7_000_000L

    val entries = TimeLineSingleStateEntries()
    entries.map["10"] = mutableListOf()
    entries.addEntry(TimeLineSingleStateEntry(ts + 1_450_000, "10", "onCreate"))
    entries.addEntry(TimeLineSingleStateEntry(ts + 2_000_000, "10", "onStart"))
    entries.addEntry(TimeLineSingleStateEntry(ts + 4_000_000, "10", "onStop"))
    entries.addEntry(TimeLineSingleStateEntry(ts + 6_000_000, "10", "onDestroy"))

    entries.map["0"] = mutableListOf()
    entries.addEntry(TimeLineSingleStateEntry(ts + 550_000, "0", "onPause"))
    entries.addEntry(TimeLineSingleStateEntry(ts + 3_023_000, "0", "onStop"))
    entries.addEntry(TimeLineSingleStateEntry(ts + 6_200_000, "0", "onDestroy"))

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
            TimelineSingleStateView(
                modifier = Modifier.fillMaxWidth().height(100.dp),
                entries = entries,
                timeFrame = timeFrame,
                showVerticalSeries = true,
                highlightedKey = "435"
            )
        }
    }
}