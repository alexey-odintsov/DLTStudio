package com.alekso.dltstudio.timeline.filters.extractors

import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltstudio.timeline.TimeLineEvent
import com.alekso.dltstudio.timeline.TimeLineEventEntry
import com.alekso.dltstudio.timeline.filters.NO_KEY
import com.alekso.dltstudio.timeline.filters.extractors.EntriesExtractor.*

class EventEntriesExtractor : EntriesExtractor {

    override fun extractEntry(
        message: DLTMessage,
        regex: Regex,
        extractionType: ExtractionType,
    ): List<TimeLineEventEntry> {
        val matches = regex.find(message.payload)!!
        val list = mutableListOf<TimeLineEventEntry>()

        when (extractionType) {
            ExtractionType.KeyValueNamed -> {
                val key: String = matches.groups[Param.KEY.value]?.value ?: NO_KEY
                val value: String? = matches.groups[Param.VALUE.value]?.value
                val info: String? = matches.groups[Param.INFO.value]?.value

                if (value != null) {
                    list.add(
                        TimeLineEventEntry(
                            message.timeStampNano,
                            key,
                            TimeLineEvent(value, info)
                        )
                    )
                }
            }

            ExtractionType.KeyValueGroups -> throw UnsupportedOperationException()
        }

        return list
    }

}
