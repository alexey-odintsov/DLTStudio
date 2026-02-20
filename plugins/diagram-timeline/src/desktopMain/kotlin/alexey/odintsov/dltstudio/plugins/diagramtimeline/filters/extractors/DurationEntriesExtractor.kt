package alexey.odintsov.dltstudio.plugins.diagramtimeline.filters.extractors

import alexey.odintsov.dltstudio.charts.model.DurationChartData
import alexey.odintsov.dltstudio.charts.model.DurationEntry
import alexey.odintsov.dltstudio.charts.model.StringKey
import alexey.odintsov.dltstudio.model.contract.LogMessage
import alexey.odintsov.dltstudio.plugins.diagramtimeline.filters.NO_KEY
import kotlin.text.get

class DurationEntriesExtractor : EntriesExtractor<DurationChartData<LogMessage>> {

    override fun extractEntry(
        message: LogMessage,
        regex: Regex,
        extractionType: EntriesExtractor.ExtractionType,
        data: DurationChartData<LogMessage>,
    ) {
        val matches = regex.find(message.dltMessage.payloadText())!!

        when (extractionType) {
            EntriesExtractor.ExtractionType.NamedGroupsOneEntry -> {
                val key: String = matches.groups[EntriesExtractor.Param.KEY.value]?.value ?: NO_KEY
                val begin: String? = matches.groups[EntriesExtractor.Param.BEGIN.value]?.value
                val end: String? = matches.groups[EntriesExtractor.Param.END.value]?.value

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

            EntriesExtractor.ExtractionType.GroupsManyEntries -> throw UnsupportedOperationException()
            EntriesExtractor.ExtractionType.NamedGroupsManyEntries -> throw UnsupportedOperationException()
        }
    }
}
