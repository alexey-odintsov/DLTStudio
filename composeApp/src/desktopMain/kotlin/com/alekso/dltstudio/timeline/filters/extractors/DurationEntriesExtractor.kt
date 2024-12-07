package com.alekso.dltstudio.timeline.filters.extractors

import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltstudio.timeline.TimeLineDurationEntry
import com.alekso.dltstudio.timeline.filters.NO_KEY
import com.alekso.dltstudio.timeline.filters.extractors.EntriesExtractor.ExtractionType
import com.alekso.dltstudio.timeline.filters.extractors.EntriesExtractor.Param

class DurationEntriesExtractor : EntriesExtractor {

    override fun extractEntry(
        message: DLTMessage,
        regex: Regex,
        extractionType: ExtractionType,
    ): List<TimeLineDurationEntry> {
        val matches = regex.find(message.payload)!!
        val list = mutableListOf<TimeLineDurationEntry>()

        when (extractionType) {
            ExtractionType.KeyValueNamed -> {
                val key: String = matches.groups[Param.KEY.value]?.value ?: NO_KEY
                val begin: String? = matches.groups[Param.BEGIN.value]?.value
                val end: String? = matches.groups[Param.END.value]?.value

                list.add(
                    TimeLineDurationEntry(message.timeStampNano, key, Pair(begin, end))
                )
            }

            ExtractionType.KeyValueGroups -> throw UnsupportedOperationException()
        }

        return list
    }
}
