package alexey.odintsov.dltstudio.plugins.diagramtimeline.filters.extractors

import alexey.odintsov.dltstudio.charts.model.PercentageChartData
import alexey.odintsov.dltstudio.charts.model.PercentageEntry
import alexey.odintsov.dltstudio.charts.model.StringKey
import alexey.odintsov.dltstudio.model.contract.LogMessage
import alexey.odintsov.dltstudio.plugins.diagramtimeline.filters.NO_KEY
import kotlin.text.get

class PercentageEntriesExtractor : EntriesExtractor<PercentageChartData<LogMessage>> {

    override fun extractEntry(
        message: LogMessage,
        regex: Regex,
        extractionType: EntriesExtractor.ExtractionType,
        data: PercentageChartData<LogMessage>,
    ) {
        val matches = regex.find(message.dltMessage.payloadText())!!
        val namedGroupsMap =
            regex.toPattern().namedGroups().entries.associateBy({ it.value }) { it.key }

        when (extractionType) {
            EntriesExtractor.ExtractionType.NamedGroupsOneEntry -> {
                val key: String = matches.groups[EntriesExtractor.Param.KEY.value]?.value ?: NO_KEY
                val value: String? = matches.groups[EntriesExtractor.Param.VALUE.value]?.value
                if (value != null) {
                    data.addEntry(
                        StringKey(key),
                        PercentageEntry(message.dltMessage.timeStampUs, value.toFloat(), message)
                    )
                }
            }

            EntriesExtractor.ExtractionType.NamedGroupsManyEntries -> {
                matches.groups.forEachIndexed { index, group ->
                    if (index > 0 && group != null) {
                        if (index < matches.groups.size) {
                            val key = namedGroupsMap[index]
                            val value = group.value
                            data.addEntry(
                                StringKey(key ?: ""),
                                PercentageEntry(
                                    message.dltMessage.timeStampUs,
                                    value.toFloat(),
                                    message
                                )
                            )
                        }
                    }
                }
            }

            EntriesExtractor.ExtractionType.GroupsManyEntries -> {
                if (matches.groups.size > 2) {
                    for (i in 1..<matches.groups.size step 2) {
                        val key = matches.groups[i]?.value
                        val value = matches.groups[i + 1]?.value
                        if (key != null && value != null) {
                            data.addEntry(
                                StringKey(key),
                                PercentageEntry(
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