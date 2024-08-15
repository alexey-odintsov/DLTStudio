package com.alekso.dltstudio.timeline.graph

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alekso.dltstudio.timeline.DiagramType
import com.alekso.dltstudio.timeline.TimeFrame
import com.alekso.dltstudio.timeline.TimeLineDurationEntries
import com.alekso.dltstudio.timeline.TimeLineDurationEntry
import com.alekso.dltstudio.timeline.TimeLineEntry
import com.alekso.dltstudio.timeline.TimeLineEvent
import com.alekso.dltstudio.timeline.TimeLineEventEntries
import com.alekso.dltstudio.timeline.TimeLineEventEntry
import com.alekso.dltstudio.timeline.TimeLineMinMaxEntries
import com.alekso.dltstudio.timeline.TimeLinePercentageEntries
import com.alekso.dltstudio.timeline.TimeLineSingleStateEntries
import com.alekso.dltstudio.timeline.TimeLineSingleStateEntry
import com.alekso.dltstudio.timeline.TimeLineStateEntries
import com.alekso.dltstudio.timeline.TimeLineStateEntry

private val timeFrame = TimeFrame(0L, 5_000_000L, 0f, 1f)

object TimelinePreviewFactory {
    @Composable
    fun getPreview(diagramType: DiagramType, modifier: Modifier) {
        when (diagramType) {
            DiagramType.Percentage -> {
                TimelinePercentageView(
                    modifier = modifier,
                    entries = TimeLinePercentageEntries().also {
                        it.addEntry(TimeLineEntry(1_000_000L, "a", 20f))
                        it.addEntry(TimeLineEntry(2_000_000L, "a", 42f))
                        it.addEntry(TimeLineEntry(3_000_000L, "a", 25f))
                        it.addEntry(TimeLineEntry(4_000_000L, "a", 79f))
                        it.addEntry(TimeLineEntry(5_000_000L, "a", 59f))
                    },
                    timeFrame = timeFrame,
                    highlightedKey = null
                )
            }

            DiagramType.MinMaxValue -> {
                TimelineMinMaxValueView(
                    modifier = modifier,
                    entries = TimeLineMinMaxEntries().also {
                        it.addEntry(TimeLineEntry(500_000L, "a", 48f))
                        it.addEntry(TimeLineEntry(1_500_000L, "a", 68f))
                        it.addEntry(TimeLineEntry(2_500_000L, "a", 55f))
                        it.addEntry(TimeLineEntry(3_500_000L, "a", 92f))
                        it.addEntry(TimeLineEntry(4_500_000L, "a", 96f))

                        it.addEntry(TimeLineEntry(1_000_000L, "b", 15f))
                        it.addEntry(TimeLineEntry(2_000_000L, "b", 20f))
                        it.addEntry(TimeLineEntry(3_000_000L, "b", 45f))
                        it.addEntry(TimeLineEntry(4_000_000L, "b", 55f))
                    },
                    timeFrame = timeFrame,
                    highlightedKey = null,
                    seriesCount = 8,
                )
            }

            DiagramType.State -> {
                TimelineStateView(
                    modifier = modifier,
                    entries = TimeLineStateEntries().also {
                        it.addEntry(TimeLineStateEntry(1_000_000L, "a", Pair("a", "b")))
                        it.addEntry(TimeLineStateEntry(2_000_000L, "a", Pair("b", "c")))
                        it.addEntry(TimeLineStateEntry(3_000_000L, "a", Pair("c", "a")))
                        it.addEntry(TimeLineStateEntry(4_000_000L, "a", Pair("a", "b")))
                    },
                    timeFrame = timeFrame,
                    highlightedKey = null
                )
            }

            DiagramType.SingleState -> {
                TimelineSingleStateView(
                    modifier = modifier,
                    entries = TimeLineSingleStateEntries().also {
                        it.addEntry(TimeLineSingleStateEntry(1_500_000L, "a", "DRIVING"))
                        it.addEntry(TimeLineSingleStateEntry(2_000_000L, "a", "STOPPED"))
                        it.addEntry(TimeLineSingleStateEntry(3_000_000L, "a", "PARKED"))
                        it.addEntry(TimeLineSingleStateEntry(4_000_000L, "a", "OFF"))
                    },
                    timeFrame = timeFrame,
                    highlightedKey = null
                )
            }

            DiagramType.Duration -> {
                TimelineDurationView(
                    modifier = modifier,
                    entries = TimeLineDurationEntries().also {
                        it.addEntry(TimeLineDurationEntry(2_000_000L, "onCreate", Pair("begin", null)))
                        it.addEntry(TimeLineDurationEntry(3_000_000L, "onCreate", Pair(null, "end")))
                        it.addEntry(TimeLineDurationEntry(3_000_000L, "onStart", Pair("begin", null)))
                        it.addEntry(TimeLineDurationEntry(4_000_000L, "onStart", Pair(null, "end")))
                    },
                    timeFrame = timeFrame,
                    highlightedKey = null
                )
            }

            DiagramType.Events -> {
                TimelineEventView(
                    modifier = modifier,
                    entries = TimeLineEventEntries().also {
                        it.addEntry(TimeLineEventEntry(2_000_000L, "app1", TimeLineEvent("CRASH", null)))
                        it.addEntry(TimeLineEventEntry(3_000_000L, "app2", TimeLineEvent("CRASH", null)))
                        it.addEntry(TimeLineEventEntry(4_000_000L, "app1", TimeLineEvent("ANR", null)))
                        it.addEntry(TimeLineEventEntry(2_500_000L, "service1", TimeLineEvent("WTF", null)))
                    },
                    timeFrame = timeFrame,
                    highlightedKey = null
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewDiagramPreviews() {
    Box {
        TimelinePreviewFactory.getPreview(DiagramType.MinMaxValue, Modifier.width(200.dp).height(200.dp))
    }
}