package alexey.odintsov.dltstudio.plugins.diagramtimeline.filters.extractors

import alexey.odintsov.dltstudio.charts.model.SingleStateChartData
import alexey.odintsov.dltstudio.charts.model.SingleStateEntry
import alexey.odintsov.dltstudio.charts.model.StringKey
import alexey.odintsov.dltstudio.model.contract.LogMessage
import alexey.odintsov.dltstudio.plugins.diagramtimeline.filters.NO_KEY

class SingleStateEntriesExtractor : EntriesExtractor<SingleStateChartData<LogMessage>> {

    override fun extractEntry(
        message: LogMessage,
        regex: Regex,
        extractionType: EntriesExtractor.ExtractionType,
        data: SingleStateChartData<LogMessage>
    ) {
        val matches = regex.find(message.dltMessage.payloadText())!!

        when (extractionType) {
            EntriesExtractor.ExtractionType.NamedGroupsOneEntry -> {
                val key: String = matches.groups[EntriesExtractor.Param.KEY.value]?.value ?: NO_KEY
                val value: String? = matches.groups[EntriesExtractor.Param.VALUE.value]?.value

                if (value != null) {
                    data.addEntry(
                        StringKey(key),
                        SingleStateEntry<LogMessage>(message.dltMessage.timeStampUs, value, message)
                    )
                }
            }

            EntriesExtractor.ExtractionType.NamedGroupsManyEntries -> throw UnsupportedOperationException()

            EntriesExtractor.ExtractionType.GroupsManyEntries -> {
                if (matches.groups.size > 2) {
                    val key = matches.groups[INDEX_KEY + 1]?.value
                    val value = matches.groups[INDEX_VALUE + 1]?.value
                    if (key != null && value != null) {
                        data.addEntry(
                            StringKey(key),
                            SingleStateEntry<LogMessage>(
                                message.dltMessage.timeStampUs,
                                value,
                                message
                            )
                        )
                    }
                }
            }
        }
    }

    companion object {
        const val INDEX_KEY = 0
        const val INDEX_VALUE = 1
    }
}
