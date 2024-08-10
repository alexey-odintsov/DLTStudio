package com.alekso.dltstudio.timeline

import com.alekso.dltstudio.timeline.filters.TimelineFilter.ExtractorParam

enum class Param {
    KEY,
    VALUE,
    OLD_VALUE,
    BEGIN,
    END,
};
enum class DiagramType(val description: String) {

    Percentage(description = "Shows values change from in 0..100% range") { // CPU Usage
        override fun createEntries(): TimeLineEntries<*> = TimeLinePercentageEntries()
        override val params: Map<Param, ExtractorParam> = mapOf(
            Param.KEY to ExtractorParam("key", "key value of the entry"),
            Param.VALUE to ExtractorParam("value", "value of the entry", required = true),
        )
    },
    MinMaxValue(description = "Shows values change from in 0..Max range") { // Memory usage
        override fun createEntries(): TimeLineEntries<*> = TimeLineMinMaxEntries()
        override val params: Map<Param, ExtractorParam> = mapOf(
            Param.KEY to ExtractorParam("key", "key value of the entry"),
            Param.VALUE to ExtractorParam("value", "value of the entry", required = true),
        )
    },
    State(description = "Shows values change from given states") { // User switch
        override fun createEntries(): TimeLineEntries<*> = TimeLineStateEntries()
        override val params: Map<Param, ExtractorParam> = mapOf(
            Param.KEY to ExtractorParam("key", "key value of the entry"),
            Param.VALUE to ExtractorParam("value", "New state value", required = true),
            Param.OLD_VALUE to ExtractorParam("oldvalue", "Previous state value", required = true),
        )
    },

    SingleState(description = "Shows states changes for single value") { // User switch
        override fun createEntries(): TimeLineEntries<*> = TimeLineSingleStateEntries()
        override val params: Map<Param, ExtractorParam> = mapOf(
            Param.KEY to ExtractorParam("key", "key value of the entry"),
            Param.VALUE to ExtractorParam("value", "New state value", required = true),
        )
    },

    Duration(description = "Shows duration bars") { // User switch
        override fun createEntries(): TimeLineEntries<*> = TimeLineDurationEntries()
        override val params: Map<Param, ExtractorParam> = mapOf(
            Param.KEY to ExtractorParam("key", "key value of the entry"),
            Param.BEGIN to ExtractorParam("begin", "No description yet"),
            Param.END to ExtractorParam("begin", "No description yet"),
        )
    },

    Events(description = "Shows events that occur over the time") {
        override fun createEntries(): TimeLineEntries<*> = TimeLineEventEntries()
        override val params: Map<Param, ExtractorParam> = mapOf(
            Param.KEY to ExtractorParam("key", "key value of the entry"),
            Param.VALUE to ExtractorParam("value", "Event value", required = true),
        )
    },
    ;

    abstract fun createEntries(): TimeLineEntries<*>
    abstract val params: Map<Param, ExtractorParam>
}