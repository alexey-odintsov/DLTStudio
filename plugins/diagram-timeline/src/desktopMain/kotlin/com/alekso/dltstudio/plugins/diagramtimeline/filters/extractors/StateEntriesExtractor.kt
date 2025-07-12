package com.alekso.dltstudio.plugins.diagramtimeline.filters.extractors

import com.alekso.dltstudio.charts.model.StateChartData
import com.alekso.dltstudio.charts.model.StateEntry
import com.alekso.dltstudio.charts.model.StringKey
import com.alekso.dltstudio.model.contract.LogMessage
import com.alekso.dltstudio.plugins.diagramtimeline.filters.NO_KEY
import com.alekso.dltstudio.plugins.diagramtimeline.filters.extractors.EntriesExtractor.ExtractionType
import com.alekso.dltstudio.plugins.diagramtimeline.filters.extractors.EntriesExtractor.Param

class StateEntriesExtractor : EntriesExtractor<StateChartData<LogMessage>> {

    override fun extractEntry(
        message: LogMessage,
        regex: Regex,
        extractionType: ExtractionType,
        data: StateChartData<LogMessage>,
    ) {
        val matches = regex.find(message.dltMessage.payloadText())!!

        when (extractionType) {
            ExtractionType.NamedGroupsOneEntry -> {
                val key: String = matches.groups[Param.KEY.value]?.value ?: NO_KEY
                val value: String? = matches.groups[Param.VALUE.value]?.value
                val oldValue: String? = matches.groups[Param.OLD_VALUE.value]?.value

                if (value != null && oldValue != null) {
                    data.addEntry(
                        StringKey(key),
                        StateEntry(message.dltMessage.timeStampUs, oldValue, value, message)
                    )
                }
            }

            ExtractionType.NamedGroupsManyEntries -> throw UnsupportedOperationException()

            ExtractionType.GroupsManyEntries -> {
                if (matches.groups.size > 2) {
                    val key = matches.groups[INDEX_KEY + 1]?.value
                    val value = matches.groups[INDEX_VALUE + 1]?.value
                    val oldValue = matches.groups[INDEX_OLD_VALUE + 1]?.value
                    if (key != null && value != null && oldValue != null) {
                        data.addEntry(
                            StringKey(key),
                            StateEntry(message.dltMessage.timeStampUs, oldValue, value, message)
                        )
                    }
                }
            }
        }
    }

    companion object {
        const val INDEX_KEY = 0
        const val INDEX_VALUE = 2
        const val INDEX_OLD_VALUE = 1
    }
}
