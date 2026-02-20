package alexey.odintsov.dltstudio.plugins.diagramtimeline

import alexey.odintsov.dltstudio.charts.model.ChartData
import alexey.odintsov.dltstudio.charts.model.DurationChartData
import alexey.odintsov.dltstudio.charts.model.EventsChartData
import alexey.odintsov.dltstudio.charts.model.MinMaxChartData
import alexey.odintsov.dltstudio.charts.model.PercentageChartData
import alexey.odintsov.dltstudio.charts.model.SingleStateChartData
import alexey.odintsov.dltstudio.charts.model.StateChartData
import alexey.odintsov.dltstudio.model.contract.LogMessage
import alexey.odintsov.dltstudio.plugins.diagramtimeline.filters.extractors.EntriesExtractor


enum class DiagramType(val description: String) {

    Percentage(description = "Shows how values change over time as a proportion of the whole. A good examples could be CPU usage.") {
        override fun createEntries(): ChartData<LogMessage> = PercentageChartData()
        override val params: Map<EntriesExtractor.Param, EntriesExtractor.ExtractorParam> = mapOf(
            EntriesExtractor.Param.KEY to EntriesExtractor.ExtractorParam(
                "key",
                "Key value of the entry."
            ),
            EntriesExtractor.Param.VALUE to EntriesExtractor.ExtractorParam(
                "value",
                "Float value of the entry."
            ),
        )
    },
    MinMaxValue(description = "Shows how values change over time within 0 and Max values. Memory usage is one example.") {
        override fun createEntries(): ChartData<LogMessage> = MinMaxChartData()
        override val params: Map<EntriesExtractor.Param, EntriesExtractor.ExtractorParam> = mapOf(
            EntriesExtractor.Param.KEY to EntriesExtractor.ExtractorParam(
                "key",
                "Key value of the entry."
            ),
            EntriesExtractor.Param.VALUE to EntriesExtractor.ExtractorParam(
                "value",
                "Numerical value of the entry"
            ),
        )
    },
    State(description = "A diagram that displays different states over time. Requires both new and old states.") {
        override fun createEntries(): ChartData<LogMessage> = StateChartData()
        override val params: Map<EntriesExtractor.Param, EntriesExtractor.ExtractorParam> = mapOf(
            EntriesExtractor.Param.KEY to EntriesExtractor.ExtractorParam(
                "key",
                "Key value of the entry"
            ),
            EntriesExtractor.Param.VALUE to EntriesExtractor.ExtractorParam(
                "value",
                "New state value"
            ),
            EntriesExtractor.Param.OLD_VALUE to EntriesExtractor.ExtractorParam(
                "oldvalue",
                "Previous state value"
            ),
        )
    },

    SingleState(description = "A diagram that displays different states over time, Relies only on new state and assumes that the previous state is in the previous entry.") {
        override fun createEntries(): ChartData<LogMessage> = SingleStateChartData()
        override val params: Map<EntriesExtractor.Param, EntriesExtractor.ExtractorParam> = mapOf(
            EntriesExtractor.Param.KEY to EntriesExtractor.ExtractorParam(
                "key",
                "Key value of the entry"
            ),
            EntriesExtractor.Param.VALUE to EntriesExtractor.ExtractorParam(
                "value",
                "New state value"
            ),
        )
    },

    Duration(description = "A chart that visualizes the length of time events or activities last. As KEY you can use event name, and as BEGIN – starting event and END - ending event.") {
        override fun createEntries(): ChartData<LogMessage> = DurationChartData()
        override val params: Map<EntriesExtractor.Param, EntriesExtractor.ExtractorParam> = mapOf(
            EntriesExtractor.Param.KEY to EntriesExtractor.ExtractorParam(
                "key",
                "Key value of the entry"
            ),
            EntriesExtractor.Param.BEGIN to EntriesExtractor.ExtractorParam(
                "begin",
                "Begin event marker"
            ),
            EntriesExtractor.Param.END to EntriesExtractor.ExtractorParam(
                "end",
                "End event marker"
            ),
        )
    },

    Events(description = "A diagram that marks specific events at different points in time. A good examples could be crashes or ANRs. As KEY you can use application name, and as VALUE - crash type.") {
        override fun createEntries(): ChartData<LogMessage> = EventsChartData()
        override val params: Map<EntriesExtractor.Param, EntriesExtractor.ExtractorParam> = mapOf(
            EntriesExtractor.Param.KEY to EntriesExtractor.ExtractorParam(
                "key",
                "Key value of the entry"
            ),
            EntriesExtractor.Param.VALUE to EntriesExtractor.ExtractorParam("value", "Event name"),
            EntriesExtractor.Param.INFO to EntriesExtractor.ExtractorParam(
                "info",
                "Additional info (currently is not used)"
            ),
        )
    },
    ;

    abstract fun createEntries(): ChartData<LogMessage>
    abstract val params: Map<EntriesExtractor.Param, EntriesExtractor.ExtractorParam>
}