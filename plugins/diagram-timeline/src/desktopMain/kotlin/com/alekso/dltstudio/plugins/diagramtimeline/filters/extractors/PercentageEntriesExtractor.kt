package com.alekso.dltstudio.plugins.diagramtimeline.filters.extractors

import com.alekso.dltmessage.DLTMessage
import com.alekso.dltstudio.plugins.diagramtimeline.TimeLineFloatEntry
import com.alekso.dltstudio.plugins.diagramtimeline.filters.NO_KEY
import com.alekso.dltstudio.plugins.diagramtimeline.filters.extractors.EntriesExtractor.ExtractionType
import com.alekso.dltstudio.plugins.diagramtimeline.filters.extractors.EntriesExtractor.Param
import kotlin.text.get

class PercentageEntriesExtractor : EntriesExtractor {

    override fun extractEntry(
        message: DLTMessage,
        regex: Regex,
        extractionType: ExtractionType,
    ): List<TimeLineFloatEntry> {
        val matches = regex.find(message.payloadText())!!
        val list = mutableListOf<TimeLineFloatEntry>()
        val namedGroupsMap =
            regex.toPattern().namedGroups().entries.associateBy({ it.value }) { it.key }

        when (extractionType) {
            ExtractionType.NamedGroupsOneEntry -> {
                val key: String = matches.groups[Param.KEY.value]?.value ?: NO_KEY
                val value: String? = matches.groups[Param.VALUE.value]?.value
                if (value != null) {
                    list.add(
                        TimeLineFloatEntry(
                            message.timeStampUs,
                            key,
                            value.toFloat()
                        )
                    )
                }
            }

            ExtractionType.NamedGroupsManyEntries -> {
                matches.groups.forEachIndexed { index, group ->
                    if (index > 0 && group != null) {
                        if (index < matches.groups.size) {
                            val key = namedGroupsMap[index]
                            val value = group.value
                            list.add(
                                TimeLineFloatEntry(
                                    message.timeStampUs,
                                    key ?: "",
                                    value.toFloat()
                                )
                            )
                        }
                    }
                }
            }

            ExtractionType.GroupsManyEntries -> {
                if (matches.groups.size > 2) {
                    for (i in 1..<matches.groups.size step 2) {
                        val key = matches.groups[i]?.value
                        val value = matches.groups[i + 1]?.value
                        if (key != null && value != null) {
                            list.add(
                                TimeLineFloatEntry(message.timeStampUs, key, value.toFloat())
                            )
                        }
                    }
                }
            }
        }

        return list
    }
}