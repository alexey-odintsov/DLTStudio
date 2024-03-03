package com.alekso.dltstudio.timeline.filters

import com.alekso.dltstudio.logs.colorfilters.FilterCriteria
import com.alekso.dltstudio.logs.colorfilters.FilterParameter


data class TimelineFilter(
    val name: String,
    val enabled: Boolean = true,
    val filters: Map<FilterParameter, FilterCriteria>,
    val extractPattern: String? = null, // regex only
    val diagramType: DiagramType,

    ) {
    enum class AnalyzerType {
        Regex,
        IndexOf
    }

    enum class DiagramType {
        Percentage, // CPU Usage
        MinMaxValue, // Memory usage
        State, // User switch
        Events, // Crash events
    }

    companion object {
        val Empty = TimelineFilter(
            name = "",
            enabled = false,
            filters = emptyMap(),
            extractPattern = null,
            diagramType = DiagramType.Events
        )
    }
}
