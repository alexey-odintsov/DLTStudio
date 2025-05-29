package com.alekso.dltstudio.plugins.diagramtimeline

import com.alekso.dltstudio.charts.model.ChartData
import com.alekso.dltstudio.charts.model.DurationChartData
import com.alekso.dltstudio.charts.model.EventsChartData
import com.alekso.dltstudio.charts.model.MinMaxChartData
import com.alekso.dltstudio.charts.model.PercentageChartData
import com.alekso.dltstudio.charts.model.SingleStateChartData
import com.alekso.dltstudio.charts.model.StateChartData
import com.alekso.dltstudio.plugins.diagramtimeline.filters.extractors.EntriesExtractor.ExtractorParam
import com.alekso.dltstudio.plugins.diagramtimeline.filters.extractors.EntriesExtractor.Param


enum class DiagramType(val description: String) {

    Percentage(description = "Shows how values change over time as a proportion of the whole. A good examples could be CPU usage.") {
        override fun createEntries(): ChartData = PercentageChartData()
        override val params: Map<Param, ExtractorParam> = mapOf(
            Param.KEY to ExtractorParam("key", "Key value of the entry."),
            Param.VALUE to ExtractorParam("value", "Float value of the entry."),
        )
    },
    MinMaxValue(description = "Shows how values change over time within 0 and Max values. Memory usage is one example.") {
        override fun createEntries(): ChartData = MinMaxChartData()
        override val params: Map<Param, ExtractorParam> = mapOf(
            Param.KEY to ExtractorParam("key", "Key value of the entry."),
            Param.VALUE to ExtractorParam("value", "Numerical value of the entry"),
        )
    },
    State(description = "A diagram that displays different states over time. Requires both new and old states.") {
        override fun createEntries(): ChartData = StateChartData()
        override val params: Map<Param, ExtractorParam> = mapOf(
            Param.KEY to ExtractorParam("key", "Key value of the entry"),
            Param.VALUE to ExtractorParam("value", "New state value"),
            Param.OLD_VALUE to ExtractorParam("oldvalue", "Previous state value"),
        )
    },

    SingleState(description = "A diagram that displays different states over time, Relies only on new state and assumes that the previous state is in the previous entry.") {
        override fun createEntries(): ChartData = SingleStateChartData()
        override val params: Map<Param, ExtractorParam> = mapOf(
            Param.KEY to ExtractorParam("key", "Key value of the entry"),
            Param.VALUE to ExtractorParam("value", "New state value"),
        )
    },

    Duration(description = "A chart that visualizes the length of time events or activities last. As KEY you can use event name, and as BEGIN – starting event and END - ending event.") {
        override fun createEntries(): ChartData = DurationChartData()
        override val params: Map<Param, ExtractorParam> = mapOf(
            Param.KEY to ExtractorParam("key", "Key value of the entry"),
            Param.BEGIN to ExtractorParam("begin", "Begin event marker"),
            Param.END to ExtractorParam("end", "End event marker"),
        )
    },

    Events(description = "A diagram that marks specific events at different points in time. A good examples could be crashes or ANRs. As KEY you can use application name, and as VALUE - crash type.") {
        override fun createEntries(): ChartData = EventsChartData()
        override val params: Map<Param, ExtractorParam> = mapOf(
            Param.KEY to ExtractorParam("key", "Key value of the entry"),
            Param.VALUE to ExtractorParam("value", "Event name"),
            Param.INFO to ExtractorParam("info", "Additional info (currently is not used)"),
        )
    },
    ;

    abstract fun createEntries(): ChartData
    abstract val params: Map<Param, ExtractorParam>
}