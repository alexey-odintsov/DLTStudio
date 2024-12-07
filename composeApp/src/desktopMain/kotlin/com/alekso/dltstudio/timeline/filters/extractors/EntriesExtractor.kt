package com.alekso.dltstudio.timeline.filters.extractors

import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltstudio.timeline.DiagramType
import com.alekso.dltstudio.timeline.TimeLineEntries
import com.alekso.dltstudio.timeline.TimeLineEntry
import com.alekso.dltstudio.timeline.filters.TimelineFilter

interface EntriesExtractor {
    interface ExtractionType

    fun extractEntry(
        message: DLTMessage,
        regex: Regex,
        extractionType: ExtractionType,
    ): List<TimeLineEntry<*>>
}


@Deprecated("Use EntriesExtractor")
interface EntriesExtractorOld {
    fun extractEntry(
        message: DLTMessage,
        filter: TimelineFilter,
        regex: Regex,
        entries: TimeLineEntries<*>
    )
}

class NonNamedEntriesExtractor : EntriesExtractorOld {
    override fun extractEntry(
        message: DLTMessage,
        filter: TimelineFilter,
        regex: Regex,
        entries: TimeLineEntries<*>
    ) {
        val matches = regex.find(message.payload)!!

        when (filter.diagramType) {
            DiagramType.Percentage -> {}
            DiagramType.MinMaxValue -> {}
            DiagramType.State -> {}
            DiagramType.SingleState -> {}
            DiagramType.Duration -> {}
            DiagramType.Events -> Unit
        }
    }
}

class NamedEntriesExtractor : EntriesExtractorOld {
    override fun extractEntry(
        message: DLTMessage,
        filter: TimelineFilter,
        regex: Regex,
        entries: TimeLineEntries<*>
    ) {
        val matches = regex.find(message.payload)!!
        val diagramParams = filter.diagramType.params

        when (filter.diagramType) {
            DiagramType.Percentage -> {}
            DiagramType.MinMaxValue -> {}
            DiagramType.State -> {}
            DiagramType.SingleState -> {}
            DiagramType.Duration -> {}
            DiagramType.Events -> {}
        }
    }

}
