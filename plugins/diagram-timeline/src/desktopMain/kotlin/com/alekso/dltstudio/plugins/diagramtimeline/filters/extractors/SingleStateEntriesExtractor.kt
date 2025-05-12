package com.alekso.dltstudio.plugins.diagramtimeline.filters.extractors

import com.alekso.dltmessage.DLTMessage
import com.alekso.dltstudio.plugins.diagramtimeline.TimeLineSingleStateEntry
import com.alekso.dltstudio.plugins.diagramtimeline.filters.NO_KEY
import com.alekso.dltstudio.plugins.diagramtimeline.filters.extractors.EntriesExtractor.ExtractionType
import com.alekso.dltstudio.plugins.diagramtimeline.filters.extractors.EntriesExtractor.Param

class SingleStateEntriesExtractor : EntriesExtractor {

    override fun extractEntry(
        message: DLTMessage,
        regex: Regex,
        extractionType: ExtractionType,
    ): List<TimeLineSingleStateEntry> {
        val matches = regex.find(message.payloadText())!!
        val list = mutableListOf<TimeLineSingleStateEntry>()

        when (extractionType) {
            ExtractionType.NamedGroupsOneEntry -> {
                val key: String = matches.groups[Param.KEY.value]?.value ?: NO_KEY
                val value: String? = matches.groups[Param.VALUE.value]?.value

                if (value != null) {
                    list.add(
                        TimeLineSingleStateEntry(message.timeStampUs, key, value)
                    )
                }
            }

            ExtractionType.NamedGroupsManyEntries -> throw UnsupportedOperationException()

            ExtractionType.GroupsManyEntries -> {
                if (matches.groups.size > 2) {
                    val key = matches.groups[INDEX_KEY + 1]?.value
                    val value = matches.groups[INDEX_VALUE + 1]?.value
                    if (key != null && value != null) {
                        list.add(
                            TimeLineSingleStateEntry(message.timeStampUs, key, value)
                        )
                    }
                }
            }
        }

        return list
    }

    companion object {
        const val INDEX_KEY = 0
        const val INDEX_VALUE = 1
    }
}
