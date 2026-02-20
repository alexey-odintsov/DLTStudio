package alexey.odintsov.dltstudio.plugins.diagramtimeline.filters.extractors

import alexey.odintsov.dltstudio.charts.model.ChartData
import alexey.odintsov.dltstudio.charts.model.DurationChartData
import alexey.odintsov.dltstudio.charts.model.EventsChartData
import alexey.odintsov.dltstudio.charts.model.MinMaxChartData
import alexey.odintsov.dltstudio.charts.model.PercentageChartData
import alexey.odintsov.dltstudio.charts.model.SingleStateChartData
import alexey.odintsov.dltstudio.charts.model.StateChartData
import alexey.odintsov.dltstudio.model.contract.LogMessage
import alexey.odintsov.dltstudio.plugins.diagramtimeline.DiagramType


interface EntriesExtractor<T : ChartData<LogMessage>> {
    enum class ExtractionType(val description: String) {
        NamedGroupsOneEntry("One entry per line, key and value are extracted from string: '<key:X> <value:y>'"),
        NamedGroupsManyEntries("Many entries per line, keys are fixed and values are extracted from string: '<key1:value1> <key2:value2>'"),
        GroupsManyEntries("Many entries per line, no named groups: '(key1) (value1) (key2) (value2)'"),
    }

    enum class Param(val value: String) {
        KEY("key"),
        VALUE("value"),
        INFO("info"),
        OLD_VALUE("oldvalue"),
        BEGIN("begin"),
        END("end"),
    }

    data class ExtractorParam(
        val key: String,
        val description: String,
        val required: Boolean = true,
    )

    fun extractEntry(
        message: LogMessage,
        regex: Regex,
        extractionType: ExtractionType,
        data: T,
    )

    companion object {
        fun analyzeEntriesRegex(
            message: LogMessage,
            diagramType: DiagramType,
            extractorType: ExtractionType,
            regex: Regex,
            entries: ChartData<LogMessage>
        ) {
            try {
                when (diagramType) {
                    DiagramType.Percentage -> {
                        PercentageEntriesExtractor().extractEntry(
                            message, regex, extractorType, entries as PercentageChartData
                        )
                    }

                    DiagramType.MinMaxValue -> {
                        MinMaxEntriesExtractor().extractEntry(
                            message, regex, extractorType, entries as MinMaxChartData
                        )
                    }

                    DiagramType.State -> {
                        StateEntriesExtractor().extractEntry(
                            message, regex, extractorType, entries as StateChartData
                        )
                    }

                    DiagramType.SingleState -> {
                        SingleStateEntriesExtractor().extractEntry(
                            message, regex, extractorType, entries as SingleStateChartData
                        )
                    }

                    DiagramType.Duration -> {
                        DurationEntriesExtractor().extractEntry(
                            message, regex, extractorType, entries as DurationChartData
                        )
                    }

                    DiagramType.Events -> {
                        EventEntriesExtractor().extractEntry(
                            message, regex, extractorType, entries as EventsChartData
                        )
                    }
                }
            } catch (_: Exception) {
                // ignore
            }
        }
    }
}
