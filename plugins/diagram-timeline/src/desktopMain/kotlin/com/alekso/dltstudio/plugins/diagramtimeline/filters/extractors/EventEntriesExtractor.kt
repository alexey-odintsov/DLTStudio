package com.alekso.dltstudio.plugins.diagramtimeline.filters.extractors

import com.alekso.dltmessage.DLTMessage
import com.alekso.dltstudio.plugins.diagramtimeline.TimeLineEvent
import com.alekso.dltstudio.plugins.diagramtimeline.TimeLineEventEntry
import com.alekso.dltstudio.plugins.diagramtimeline.filters.NO_KEY
import com.alekso.dltstudio.plugins.diagramtimeline.filters.extractors.EntriesExtractor.*

class EventEntriesExtractor : EntriesExtractor {

    override fun extractEntry(
        message: DLTMessage,
        regex: Regex,
        extractionType: ExtractionType,
    ): List<TimeLineEventEntry> {
        val matches = regex.find(message.payloadText())!!
        val list = mutableListOf<TimeLineEventEntry>()

        when (extractionType) {
            ExtractionType.NamedGroupsOneEntry -> {
                val key: String = matches.groups[Param.KEY.value]?.value ?: NO_KEY
                val value: String? = matches.groups[Param.VALUE.value]?.value
                val info: String? = matches.groups[Param.INFO.value]?.value

                if (value != null) {
                    list.add(
                        TimeLineEventEntry(
                            message.timeStampUs,
                            key,
                            TimeLineEvent(value, info)
                        )
                    )
                }
            }

            ExtractionType.GroupsManyEntries -> throw UnsupportedOperationException()
            ExtractionType.NamedGroupsManyEntries -> throw UnsupportedOperationException()
        }

        return list
    }

}
