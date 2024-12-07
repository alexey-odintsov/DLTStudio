package com.alekso.dltstudio.timeline.filters.extractors

import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltstudio.timeline.TimeLineDurationEntry
import com.alekso.dltstudio.timeline.TimeLineEntry
import com.alekso.dltstudio.timeline.filters.NO_KEY
import com.alekso.dltstudio.timeline.filters.extractors.EntriesExtractor.ExtractionType
import kotlin.text.get

class DurationEntriesExtractor : EntriesExtractor {
    enum class DurationExtractionType : ExtractionType {
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
            DurationExtractionType.NAMED_GROUPS -> {
                val key: String = matches.groups[NAME_KEY]?.value ?: NO_KEY
                val begin: String? = matches.groups[BEGIN_VALUE]?.value
                val end: String? = matches.groups[END_VALUE]?.value

                list.add(
                    TimeLineDurationEntry(message.timeStampNano, key, Pair(begin, end))
                )
            }
        }

        return list
    }

    companion object {
        const val NAME_KEY = "key"
        const val BEGIN_VALUE = "begin"
        const val END_VALUE = "end"
    }
}