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
        val namedGroupsMap =
            regex.toPattern().namedGroups().entries.associateBy({ it.value }) { it.key }

        when (extractionType) {
            StateExtractionType.NAMED_GROUPS -> {
                matches.groups.forEachIndexed { index, group ->
                    if (index > 0 && group != null) {
                        if (index < matches.groups.size) {
                            val key: String = matches.groups["key"]?.value ?: NO_KEY
                            val value: String? = matches.groups["value"]?.value
                            val oldValue: String? = matches.groups["oldvalue"]?.value

                            if (key != null && value != null && oldValue != null) {
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
                    val key = matches.groups[1]?.value
                    val value = matches.groups[2]?.value
                    val oldValue = matches.groups[3]?.value
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
}