package alexey.odintsov.dltstudio.plugins.diagramtimeline.filters.extractors

import alexey.odintsov.dltstudio.charts.model.EventEntry
import alexey.odintsov.dltstudio.charts.model.EventsChartData
import alexey.odintsov.dltstudio.charts.model.StringKey
import alexey.odintsov.dltstudio.model.contract.LogMessage
import alexey.odintsov.dltstudio.plugins.diagramtimeline.filters.NO_KEY
import kotlin.text.get

class EventEntriesExtractor : EntriesExtractor<EventsChartData<LogMessage>> {

    override fun extractEntry(
        message: LogMessage,
        regex: Regex,
        extractionType: EntriesExtractor.ExtractionType,
        data: EventsChartData<LogMessage>
    ) {
        val matches = regex.find(message.dltMessage.payloadText())!!

        when (extractionType) {
            EntriesExtractor.ExtractionType.NamedGroupsOneEntry -> {
                val key: String = matches.groups[EntriesExtractor.Param.KEY.value]?.value ?: NO_KEY
                val value: String? = matches.groups[EntriesExtractor.Param.VALUE.value]?.value
                matches.groups[EntriesExtractor.Param.INFO.value]?.value

                if (value != null) {
                    data.addEntry(
                        StringKey(key),
                        EventEntry(message.dltMessage.timeStampUs, value, message)
                    )
                }
            }

            EntriesExtractor.ExtractionType.GroupsManyEntries -> throw UnsupportedOperationException()
            EntriesExtractor.ExtractionType.NamedGroupsManyEntries -> throw UnsupportedOperationException()
        }
    }

}
