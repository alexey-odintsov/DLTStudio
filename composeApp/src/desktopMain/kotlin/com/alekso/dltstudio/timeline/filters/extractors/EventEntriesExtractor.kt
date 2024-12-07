package com.alekso.dltstudio.timeline.filters.extractors

import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltstudio.timeline.TimeLineEntry
import com.alekso.dltstudio.timeline.TimeLineEvent
import com.alekso.dltstudio.timeline.TimeLineEventEntry
import com.alekso.dltstudio.timeline.filters.NO_KEY
import com.alekso.dltstudio.timeline.filters.extractors.EntriesExtractor.ExtractionType

class EventEntriesExtractor : EntriesExtractor {
    enum class EventExtractionType : ExtractionType {
        NAMED_GROUPS,
    }

    override fun extractEntry(
        message: DLTMessage,
        regex: Regex,
        extractionType: ExtractionType,
    ): List<TimeLineEntry<*>> {
        val matches = regex.find(message.payload)!!
        val list = mutableListOf<TimeLineEntry<*>>()

        when (extractionType) {
            EventExtractionType.NAMED_GROUPS -> {
                val key: String = matches.groups[NAME_KEY]?.value ?: NO_KEY
                val value: String? = matches.groups[NAME_VALUE]?.value
                val info: String? = matches.groups[NAME_INFO]?.value

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
        }

        return list
    }

    companion object {
        const val NAME_KEY = "key"
        const val NAME_VALUE = "value"
        const val NAME_INFO = "info"
    }
}