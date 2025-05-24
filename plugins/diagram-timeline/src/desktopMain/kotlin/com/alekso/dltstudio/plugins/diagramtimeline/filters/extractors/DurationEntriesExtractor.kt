package com.alekso.dltstudio.plugins.diagramtimeline.filters.extractors

import com.alekso.dltmessage.DLTMessage
import com.alekso.dltstudio.charts.model.DurationChartData
import com.alekso.dltstudio.charts.model.DurationEntry
import com.alekso.dltstudio.charts.model.StringKey
import com.alekso.dltstudio.plugins.diagramtimeline.filters.NO_KEY
import com.alekso.dltstudio.plugins.diagramtimeline.filters.extractors.EntriesExtractor.ExtractionType
import com.alekso.dltstudio.plugins.diagramtimeline.filters.extractors.EntriesExtractor.Param

class DurationEntriesExtractor : EntriesExtractor<DurationChartData> {

    override fun extractEntry(
        message: DLTMessage,
        regex: Regex,
        extractionType: ExtractionType,
        data: DurationChartData,
    ) {
        val matches = regex.find(message.payloadText())!!

        when (extractionType) {
            ExtractionType.NamedGroupsOneEntry -> {
                val key: String = matches.groups[Param.KEY.value]?.value ?: NO_KEY
                val begin: String? = matches.groups[Param.BEGIN.value]?.value
                val end: String? = matches.groups[Param.END.value]?.value

                // TODO: To build Duration entries from different begin and end value
                data.addEntry(
                    StringKey(key),
                    DurationEntry(message.timeStampUs, begin = begin, end = end, data = null)
                )
            }

            ExtractionType.GroupsManyEntries -> throw UnsupportedOperationException()
            ExtractionType.NamedGroupsManyEntries -> throw UnsupportedOperationException()
        }
    }
}
