package com.alekso.dltstudio.plugins.diagramtimeline.filters.extractors

import com.alekso.dltstudio.charts.model.MinMaxChartData
import com.alekso.dltstudio.charts.model.MinMaxEntry
import com.alekso.dltstudio.charts.model.StringKey
import com.alekso.dltstudio.model.contract.LogMessage
import com.alekso.dltstudio.plugins.diagramtimeline.filters.NO_KEY
import com.alekso.dltstudio.plugins.diagramtimeline.filters.extractors.EntriesExtractor.ExtractionType
import com.alekso.dltstudio.plugins.diagramtimeline.filters.extractors.EntriesExtractor.Param

class MinMaxEntriesExtractor : EntriesExtractor<MinMaxChartData<LogMessage>> {
    override fun extractEntry(
        message: LogMessage,
        regex: Regex,
        extractionType: ExtractionType,
        data: MinMaxChartData<LogMessage>,
    ) {
        val matches = regex.find(message.dltMessage.payloadText())!!
        val namedGroupsMap =
            regex.toPattern().namedGroups().entries.associateBy({ it.value }) { it.key }

        when (extractionType) {
            ExtractionType.NamedGroupsOneEntry -> {
                val key: String = matches.groups[Param.KEY.value]?.value ?: NO_KEY
                val value: String? = matches.groups[Param.VALUE.value]?.value
                if (value != null) {
                    data.addEntry(
                        StringKey(key),
                        MinMaxEntry(message.dltMessage.timeStampUs, value.toFloat(), message)
                    )
                }
            }

            ExtractionType.NamedGroupsManyEntries -> {
                matches.groups.forEachIndexed { index, group ->
                    if (index > 0 && group != null) {
                        if (index < matches.groups.size) {
                            val key = namedGroupsMap[index]
                            val value = group.value
                            data.addEntry(
                                StringKey(key ?: ""),
                                MinMaxEntry(
                                    message.dltMessage.timeStampUs,
                                    value.toFloat(),
                                    message
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
                            data.addEntry(
                                StringKey(key),
                                MinMaxEntry(
                                    message.dltMessage.timeStampUs,
                                    value.toFloat(),
                                    message
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}