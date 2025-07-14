package com.alekso.dltstudio.plugins.diagramtimeline.filters.extractors

import com.alekso.dltstudio.charts.model.DurationChartData
import com.alekso.dltstudio.charts.model.DurationEntry
import com.alekso.dltstudio.charts.model.StringKey
import com.alekso.dltstudio.model.contract.LogMessage
import com.alekso.dltstudio.plugins.diagramtimeline.filters.NO_KEY
import com.alekso.dltstudio.plugins.diagramtimeline.filters.extractors.EntriesExtractor.ExtractionType
import com.alekso.dltstudio.plugins.diagramtimeline.filters.extractors.EntriesExtractor.Param

class DurationEntriesExtractor : EntriesExtractor<DurationChartData<LogMessage>> {

    override fun extractEntry(
        message: LogMessage,
        regex: Regex,
        extractionType: ExtractionType,
        data: DurationChartData<LogMessage>,
    ) {
        val matches = regex.find(message.dltMessage.payloadText())!!

        when (extractionType) {
            ExtractionType.NamedGroupsOneEntry -> {
                val key: String = matches.groups[Param.KEY.value]?.value ?: NO_KEY
                val begin: String? = matches.groups[Param.BEGIN.value]?.value
                val end: String? = matches.groups[Param.END.value]?.value

                data.addEntry(
                    StringKey(key),
                    DurationEntry(
                        message.dltMessage.timeStampUs,
                        begin = begin,
                        end = end,
                        data = message
                    )
                )
            }

            ExtractionType.GroupsManyEntries -> throw UnsupportedOperationException()
            ExtractionType.NamedGroupsManyEntries -> throw UnsupportedOperationException()
        }
    }
}
