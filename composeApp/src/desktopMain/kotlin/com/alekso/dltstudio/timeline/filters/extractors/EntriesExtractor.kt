package com.alekso.dltstudio.timeline.filters.extractors

import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltstudio.timeline.DiagramType
import com.alekso.dltstudio.timeline.Param
import com.alekso.dltstudio.timeline.TimeLineDurationEntries
import com.alekso.dltstudio.timeline.TimeLineDurationEntry
import com.alekso.dltstudio.timeline.TimeLineEntries
import com.alekso.dltstudio.timeline.TimeLineEntry
import com.alekso.dltstudio.timeline.TimeLineEvent
import com.alekso.dltstudio.timeline.TimeLineEventEntries
import com.alekso.dltstudio.timeline.TimeLineEventEntry
import com.alekso.dltstudio.timeline.TimeLineSingleStateEntries
import com.alekso.dltstudio.timeline.TimeLineSingleStateEntry
import com.alekso.dltstudio.timeline.filters.NO_KEY
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

            DiagramType.Events -> {
                val key: String = matches.groups[diagramParams[Param.KEY]?.key!!]?.value ?: NO_KEY
                val value: String? = matches.groups[diagramParams[Param.VALUE]?.key!!]?.value

                if (value != null) {
                    (entries as TimeLineEventEntries).addEntry(
                        TimeLineEventEntry(
                            message.timeStampNano,
                            key,
                            TimeLineEvent(value, null) // todo: We don't use any info yet
                        )
                    )
                }
            }
        }
    }

}
