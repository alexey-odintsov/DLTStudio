package com.alekso.dltstudio.plugins.diagramtimeline.graph

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alekso.dltstudio.charts.model.TimeFrame
import com.alekso.dltstudio.plugins.diagramtimeline.DiagramType

private val timeFrame = TimeFrame(0L, 5_000_000L)

object TimelinePreviewFactory {
    @Composable
    fun getPreview(diagramType: DiagramType, modifier: Modifier) {
//        when (diagramType) {
//            DiagramType.Percentage -> {
//                TimelinePercentageView(
//                    modifier = modifier,
//                    entries = TimeLinePercentageEntries().also {
//                        it.addEntry(TimeLineFloatEntry(1_000_000L, "a", 20f))
//                        it.addEntry(TimeLineFloatEntry(2_000_000L, "a", 42f))
//                        it.addEntry(TimeLineFloatEntry(3_000_000L, "a", 25f))
//                        it.addEntry(TimeLineFloatEntry(4_000_000L, "a", 79f))
//                        it.addEntry(TimeLineFloatEntry(5_000_000L, "a", 59f))
//                    },
//                    timeFrame = timeFrame,
//                    highlightedKey = null
//                )
//            }
//
//            DiagramType.MinMaxValue -> {
//                TimelineMinMaxValueView(
//                    modifier = modifier,
//                    entries = TimeLineMinMaxEntries().also {
//                        it.addEntry(TimeLineFloatEntry(500_000L, "a", 48f))
//                        it.addEntry(TimeLineFloatEntry(1_500_000L, "a", 68f))
//                        it.addEntry(TimeLineFloatEntry(2_500_000L, "a", 55f))
//                        it.addEntry(TimeLineFloatEntry(3_500_000L, "a", 92f))
//                        it.addEntry(TimeLineFloatEntry(4_500_000L, "a", 96f))
//
//                        it.addEntry(TimeLineFloatEntry(1_000_000L, "b", 15f))
//                        it.addEntry(TimeLineFloatEntry(2_000_000L, "b", 20f))
//                        it.addEntry(TimeLineFloatEntry(3_000_000L, "b", 45f))
//                        it.addEntry(TimeLineFloatEntry(4_000_000L, "b", 55f))
//                    },
//                    timeFrame = timeFrame,
//                    highlightedKey = null,
//                    seriesCount = 8,
//                )
//            }
//
//            DiagramType.State -> {
//                TimelineStateView(
//                    modifier = modifier,
//                    entries = TimeLineStateEntries().also {
//                        it.addEntry(TimeLineStateEntry(1_000_000L, "a", Pair("a", "b")))
//                        it.addEntry(TimeLineStateEntry(2_000_000L, "a", Pair("b", "c")))
//                        it.addEntry(TimeLineStateEntry(3_000_000L, "a", Pair("c", "a")))
//                        it.addEntry(TimeLineStateEntry(4_000_000L, "a", Pair("a", "b")))
//                    },
//                    timeFrame = timeFrame,
//                    highlightedKey = null
//                )
//            }
//
//            DiagramType.SingleState -> {
//                TimelineSingleStateView(
//                    modifier = modifier,
//                    entries = TimeLineSingleStateEntries().also {
//                        it.addEntry(TimeLineSingleStateEntry(1_500_000L, "a", "DRIVING"))
//                        it.addEntry(TimeLineSingleStateEntry(2_000_000L, "a", "STOPPED"))
//                        it.addEntry(TimeLineSingleStateEntry(3_000_000L, "a", "PARKED"))
//                        it.addEntry(TimeLineSingleStateEntry(4_000_000L, "a", "OFF"))
//                    },
//                    timeFrame = timeFrame,
//                    highlightedKey = null
//                )
//            }
//
//            DiagramType.Duration -> {
//                TimelineDurationView(
//                    modifier = modifier,
//                    entries = TimeLineDurationEntries().also {
//                        it.addEntry(TimeLineDurationEntry(2_000_000L, "onCreate", Pair("begin", null)))
//                        it.addEntry(TimeLineDurationEntry(3_000_000L, "onCreate", Pair(null, "end")))
//                        it.addEntry(TimeLineDurationEntry(3_000_000L, "onStart", Pair("begin", null)))
//                        it.addEntry(TimeLineDurationEntry(4_000_000L, "onStart", Pair(null, "end")))
//                    },
//                    timeFrame = timeFrame,
//                    highlightedKey = null
//                )
//            }
//
//            DiagramType.Events -> {
//                TimelineEventView(
//                    modifier = modifier,
//                    entries = TimeLineEventEntries().also {
//                        it.addEntry(TimeLineEventEntry(2_000_000L, "app1", TimeLineEvent("CRASH", null)))
//                        it.addEntry(TimeLineEventEntry(3_000_000L, "app2", TimeLineEvent("CRASH", null)))
//                        it.addEntry(TimeLineEventEntry(4_000_000L, "app1", TimeLineEvent("ANR", null)))
//                        it.addEntry(TimeLineEventEntry(2_500_000L, "service1", TimeLineEvent("WTF", null)))
//                    },
//                    timeFrame = timeFrame,
//                    highlightedKey = null
//                )
//            }
//        }
    }
}

@Preview
@Composable
fun PreviewDiagramPreviews() {
    Box {
        TimelinePreviewFactory.getPreview(
            DiagramType.MinMaxValue,
            Modifier.width(200.dp).height(200.dp)
        )
    }
}