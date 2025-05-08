package com.alekso.dltstudio.plugins.diagramtimeline.filters.extractors

import com.alekso.dltmessage.DLTMessage
import com.alekso.dltstudio.plugins.diagramtimeline.DiagramType
import com.alekso.dltstudio.plugins.diagramtimeline.TimeLineDurationEntries
import com.alekso.dltstudio.plugins.diagramtimeline.TimeLineEntries
import com.alekso.dltstudio.plugins.diagramtimeline.TimeLineEntry
import com.alekso.dltstudio.plugins.diagramtimeline.TimeLineEventEntries
import com.alekso.dltstudio.plugins.diagramtimeline.TimeLineMinMaxEntries
import com.alekso.dltstudio.plugins.diagramtimeline.TimeLinePercentageEntries
import com.alekso.dltstudio.plugins.diagramtimeline.TimeLineSingleStateEntries
import com.alekso.dltstudio.plugins.diagramtimeline.TimeLineStateEntries

interface EntriesExtractor {
    enum class ExtractionType(val description: String) {
        NamedGroupsOneEntry("One entry per line, key and value are extracted from string: '<key:X> <value:y>'"),
        NamedGroupsManyEntries("Many entries per line, keys are fixed and values are extracted from string: '<key1:value1> <key2:value2>'"),
        GroupsManyEntries("Many entries per line, no named groups: '(key1) (value1) (key2) (value2)'"),
    }

    enum class Param(val value: String) {
        KEY("key"),
        VALUE("value"),
        INFO("info"),
        OLD_VALUE("oldvalue"),
        BEGIN("begin"),
        END("end"),
    }

    data class ExtractorParam(
        val key: String,
        val description: String,
        val required: Boolean = true,
    )

    fun extractEntry(
        message: DLTMessage,
        regex: Regex,
        extractionType: ExtractionType,
    ): List<TimeLineEntry<*>>

    companion object {
        fun analyzeEntriesRegex(
            message: DLTMessage,
            diagramType: DiagramType,
            extractorType: ExtractionType,
            regex: Regex,
            entries: TimeLineEntries<*>
        ) {
            try {
                when (diagramType) {
                    DiagramType.Percentage -> {
                        PercentageEntriesExtractor().extractEntry(
                            message, regex, extractorType
                        ).forEach { e -> (entries as TimeLinePercentageEntries).addEntry(e) }
                    }

                    DiagramType.MinMaxValue -> {
                        MinMaxEntriesExtractor().extractEntry(
                            message, regex, extractorType
                        ).forEach { e -> (entries as TimeLineMinMaxEntries).addEntry(e) }
                    }

                    DiagramType.State -> {
                        StateEntriesExtractor().extractEntry(
                            message, regex, extractorType
                        ).forEach { e -> (entries as TimeLineStateEntries).addEntry(e) }
                    }

                    DiagramType.SingleState -> {
                        SingleStateEntriesExtractor().extractEntry(
                            message, regex, extractorType
                        ).forEach { e -> (entries as TimeLineSingleStateEntries).addEntry(e) }
                    }

                    DiagramType.Duration -> {
                        DurationEntriesExtractor().extractEntry(
                            message, regex, extractorType
                        ).forEach { e -> (entries as TimeLineDurationEntries).addEntry(e) }
                    }

                    DiagramType.Events -> {
                        EventEntriesExtractor().extractEntry(
                            message, regex, extractorType
                        ).forEach { e -> (entries as TimeLineEventEntries).addEntry(e) }
                    }
                }
            } catch (e: Exception) {
                // ignore
            }
        }
    }
}
