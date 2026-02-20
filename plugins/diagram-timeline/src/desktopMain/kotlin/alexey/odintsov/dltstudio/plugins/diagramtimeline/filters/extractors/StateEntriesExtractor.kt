package alexey.odintsov.dltstudio.plugins.diagramtimeline.filters.extractors

import alexey.odintsov.dltstudio.charts.model.StateChartData
import alexey.odintsov.dltstudio.charts.model.StateEntry
import alexey.odintsov.dltstudio.charts.model.StringKey
import alexey.odintsov.dltstudio.model.contract.LogMessage
import alexey.odintsov.dltstudio.plugins.diagramtimeline.filters.NO_KEY
import kotlin.text.get

class StateEntriesExtractor : EntriesExtractor<StateChartData<LogMessage>> {

    override fun extractEntry(
        message: LogMessage,
        regex: Regex,
        extractionType: EntriesExtractor.ExtractionType,
        data: StateChartData<LogMessage>,
    ) {
        val matches = regex.find(message.dltMessage.payloadText())!!

        when (extractionType) {
            EntriesExtractor.ExtractionType.NamedGroupsOneEntry -> {
                val key: String = matches.groups[EntriesExtractor.Param.KEY.value]?.value ?: NO_KEY
                val value: String? = matches.groups[EntriesExtractor.Param.VALUE.value]?.value
                val oldValue: String? = matches.groups[EntriesExtractor.Param.OLD_VALUE.value]?.value

                if (value != null && oldValue != null) {
                    data.addEntry(
                        StringKey(key),
                        StateEntry(message.dltMessage.timeStampUs, oldValue, value, message)
                    )
                }
            }

            EntriesExtractor.ExtractionType.NamedGroupsManyEntries -> throw UnsupportedOperationException()

            EntriesExtractor.ExtractionType.GroupsManyEntries -> {
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
