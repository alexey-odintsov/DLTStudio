package com.alekso.dltstudio.timeline.filters

import com.alekso.dltstudio.timeline.DiagramType
import com.alekso.dltstudio.timeline.filters.extractors.DurationEntriesExtractor
import com.alekso.dltstudio.timeline.filters.extractors.EntriesExtractor
import com.alekso.dltstudio.timeline.filters.extractors.EventEntriesExtractor
import com.alekso.dltstudio.timeline.filters.extractors.MinMaxEntriesExtractor
import com.alekso.dltstudio.timeline.filters.extractors.PercentageEntriesExtractor
import com.alekso.dltstudio.timeline.filters.extractors.SingleStateEntriesExtractor
import com.alekso.dltstudio.timeline.filters.extractors.StateEntriesExtractor
import com.alekso.dltstudio.utils.SampleData

object ExtractorChecker {
    fun testRegex(
        extractPattern: String?,
        testPayload: String?,
        extractorType: EntriesExtractor.ExtractionType,
        diagramType: DiagramType,
        global: Boolean = false
    ): String {
        val testMessage = SampleData.create(payloadText = testPayload)
        var groupsTestValue = ""
        if (extractPattern != null && testPayload != null) {
            val extractPatternRegex = Regex(extractPattern)
            try {
                when (diagramType) {
                    DiagramType.Percentage -> {
                        PercentageEntriesExtractor().extractEntry(
                            testMessage, extractPatternRegex, extractorType
                        ).map { e ->
                            groupsTestValue = "${e.key} -> ${e.value}"
                        }
                    }

                    DiagramType.MinMaxValue -> {
                        MinMaxEntriesExtractor().extractEntry(
                            testMessage, extractPatternRegex, extractorType
                        ).map { e ->
                            groupsTestValue = "${e.key} -> ${e.value}"
                        }
                    }

                    DiagramType.State -> {
                        StateEntriesExtractor().extractEntry(
                            testMessage, extractPatternRegex, extractorType
                        ).map { e ->
                            groupsTestValue = "${e.key} -> ${e.value.first} / ${e.value.second}"
                        }
                    }


                    DiagramType.SingleState -> {
                        SingleStateEntriesExtractor().extractEntry(
                            testMessage, extractPatternRegex, extractorType
                        ).map { e ->
                            groupsTestValue = "${e.key} -> ${e.value}"
                        }
                    }

                    DiagramType.Duration -> {
                        DurationEntriesExtractor().extractEntry(
                            testMessage, extractPatternRegex, extractorType
                        ).map { e ->
                            groupsTestValue = "${e.key} -> ${e.value.first} / ${e.value.second}"
                        }
                    }

                    DiagramType.Events -> {
                        EventEntriesExtractor().extractEntry(
                            testMessage, extractPatternRegex, extractorType
                        ).map { e ->
                            groupsTestValue = "${e.key} -> ${e.value} / ${e.value.info}"
                        }
                    }
                }
            } catch (e: Exception) {
                groupsTestValue = "Invalid regex ${e.printStackTrace()}"
            }
        }
        return groupsTestValue
    }
}