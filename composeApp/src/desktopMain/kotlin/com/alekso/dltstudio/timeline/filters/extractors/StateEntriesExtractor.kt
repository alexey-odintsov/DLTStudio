package com.alekso.dltstudio.timeline.filters.extractors

import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltstudio.timeline.TimeLineEntry
import com.alekso.dltstudio.timeline.TimeLineStateEntry
import com.alekso.dltstudio.timeline.filters.NO_KEY
import com.alekso.dltstudio.timeline.filters.extractors.EntriesExtractor.ExtractionType

class StateEntriesExtractor : EntriesExtractor {
    enum class StateExtractionType : ExtractionType {
        NAMED_GROUPS,
        GROUPS_KEY_VALUE,
    }

    override fun extractEntry(
        message: DLTMessage,
        regex: Regex,
        extractionType: ExtractionType,
    ): List<TimeLineEntry<*>> {
        val matches = regex.find(message.payload)!!
        val list = mutableListOf<TimeLineEntry<*>>()

        when (extractionType) {
            StateExtractionType.NAMED_GROUPS -> {
                matches.groups.forEachIndexed { index, group ->
                    if (index > 0 && group != null) {
                        if (index < matches.groups.size) {
                            val key: String = matches.groups[NAME_KEY]?.value ?: NO_KEY
                            val value: String? = matches.groups[NAME_VALUE]?.value
                            val oldValue: String? = matches.groups[NAME_OLD_VALUE]?.value

                            if (value != null && oldValue != null) {
                                list.add(
                                    TimeLineStateEntry(
                                        message.timeStampNano,
                                        key,
                                        Pair(value, oldValue)
                                    )
                                )
                            }
                        }

                    }
                }
            }

            StateExtractionType.GROUPS_KEY_VALUE -> {
                if (matches.groups.size > 2) {
                    val key = matches.groups[INDEX_KEY + 1]?.value
                    val value = matches.groups[INDEX_VALUE + 1]?.value
                    val oldValue = matches.groups[INDEX_OLD_VALUE + 1]?.value
                    if (key != null && value != null && oldValue != null) {
                        list.add(
                            TimeLineStateEntry(message.timeStampNano, key, Pair(value, oldValue))
                        )
                    }
                }
            }
        }

        return list
    }

    companion object {
        const val NAME_KEY = "key"
        const val NAME_VALUE = "value"
        const val NAME_OLD_VALUE = "oldvalue"
        const val INDEX_KEY = 0
        const val INDEX_VALUE = 1
        const val INDEX_OLD_VALUE = 2
    }
}