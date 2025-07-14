package com.alekso.dltstudio.plugins.diagramtimeline.filters.extractors

import com.alekso.dltstudio.charts.model.EventEntry
import com.alekso.dltstudio.charts.model.EventsChartData
import com.alekso.dltstudio.charts.model.StringKey
import com.alekso.dltstudio.model.contract.LogMessage
import com.alekso.dltstudio.plugins.diagramtimeline.filters.NO_KEY
import com.alekso.dltstudio.plugins.diagramtimeline.filters.extractors.EntriesExtractor.ExtractionType
import com.alekso.dltstudio.plugins.diagramtimeline.filters.extractors.EntriesExtractor.Param

class EventEntriesExtractor : EntriesExtractor<EventsChartData<LogMessage>> {

    override fun extractEntry(
        message: LogMessage,
        regex: Regex,
        extractionType: ExtractionType,
        data: EventsChartData<LogMessage>
    ) {
        val matches = regex.find(message.dltMessage.payloadText())!!

        when (extractionType) {
            ExtractionType.NamedGroupsOneEntry -> {
                val key: String = matches.groups[Param.KEY.value]?.value ?: NO_KEY
                val value: String? = matches.groups[Param.VALUE.value]?.value
                val info: String? = matches.groups[Param.INFO.value]?.value

                if (value != null) {
                    data.addEntry(
                        StringKey(key),
                        EventEntry(message.dltMessage.timeStampUs, value, message)
                    )
                }
            }

            ExtractionType.GroupsManyEntries -> throw UnsupportedOperationException()
            ExtractionType.NamedGroupsManyEntries -> throw UnsupportedOperationException()
        }
    }

}
