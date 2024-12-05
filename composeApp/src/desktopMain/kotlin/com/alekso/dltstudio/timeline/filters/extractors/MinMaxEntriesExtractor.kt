package com.alekso.dltstudio.timeline.filters.extractors

import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltstudio.timeline.TimeLineEntry
import com.alekso.dltstudio.timeline.TimeLineFloatEntry
import com.alekso.dltstudio.timeline.filters.extractors.EntriesExtractor.ExtractionType

class MinMaxEntriesExtractor : EntriesExtractor {
    enum class MinMaxExtractionType : ExtractionType {
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
            MinMaxExtractionType.NAMED_GROUPS -> {
                matches.groups.forEachIndexed { index, group ->
                    if (index > 0 && group != null) {
                        if (index < matches.groups.size) {
                            val key = namedGroupsMap[index]
                            val value = group.value
                            list.add(
                                TimeLineFloatEntry(
                                    message.timeStampNano,
                                    key ?: "",
                                    value.toFloat()
                                )
                            )
                        }

                    }
                }
            }

            MinMaxExtractionType.GROUPS_KEY_VALUE -> {
                if (matches.groups.size > 2) {
                    for (i in 1..<matches.groups.size step 2) {
                        val key = matches.groups[i]?.value
                        val value = matches.groups[i + 1]?.value
                        if (key != null && value != null) {
                            list.add(
                                TimeLineFloatEntry(message.timeStampNano, key, value.toFloat())
                            )
                        }
                    }
                }
                matches.groups.forEachIndexed { index, group ->

                    if (index > 0 && group != null) {
                        if (index < matches.groups.size) {
                            val key = namedGroupsMap[index]
                            val value = group.value
                            if (key != null && value != null) {
                                list.add(
                                    TimeLineFloatEntry(message.timeStampNano, key, value.toFloat())
                                )
                            }
                        }
                    }

                }
            }
        }

        return list
    }
}