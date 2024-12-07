package com.alekso.dltstudio.timeline.filters.extractors

import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltstudio.timeline.TimeLineFloatEntry
import com.alekso.dltstudio.timeline.filters.extractors.EntriesExtractor.ExtractionType

class MinMaxEntriesExtractor : EntriesExtractor {
    override fun extractEntry(
        message: DLTMessage,
        regex: Regex,
        extractionType: ExtractionType,
    ): List<TimeLineFloatEntry> {
        val matches = regex.find(message.payload)!!
        val list = mutableListOf<TimeLineFloatEntry>()
        val namedGroupsMap =
            regex.toPattern().namedGroups().entries.associateBy({ it.value }) { it.key }

        when (extractionType) {
            ExtractionType.KeyValueNamed -> {
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

            ExtractionType.KeyValueGroups -> {
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
            }
        }

        return list
    }
}