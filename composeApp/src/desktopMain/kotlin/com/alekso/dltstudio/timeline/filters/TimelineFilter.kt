package com.alekso.dltstudio.timeline.filters

import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltstudio.logs.filtering.FilterCriteria
import com.alekso.dltstudio.logs.filtering.FilterParameter
import com.alekso.dltstudio.logs.filtering.checkTextCriteria
import com.alekso.dltstudio.timeline.TimeLineDurationEntries
import com.alekso.dltstudio.timeline.TimeLineEntries
import com.alekso.dltstudio.timeline.TimeLineEventEntries
import com.alekso.dltstudio.timeline.TimeLineMinMaxEntries
import com.alekso.dltstudio.timeline.TimeLinePercentageEntries
import com.alekso.dltstudio.timeline.TimeLineSingleStateEntries
import com.alekso.dltstudio.timeline.TimeLineStateEntries

const val NO_KEY = "no_key"


data class TimelineFilter(
    val name: String,
    val enabled: Boolean = true,
    val filters: Map<FilterParameter, FilterCriteria>,
    val extractPattern: String? = null, // regex only
    val diagramType: DiagramType,
    val extractorType: ExtractorType,
    val testClause: String? = null,
    ) {

    val key: String = "$filters$extractPattern".hashCode().toString()

    data class ExtractorParam(
        val key: String,
        val description: String,
        val required: Boolean = false,
    )

    enum class DiagramType(val description: String) {
        Percentage(description = "Shows values change from in 0..100% range") { // CPU Usage
            override fun createEntries(): TimeLineEntries<*> = TimeLinePercentageEntries()
        },
        MinMaxValue(description = "Shows values change from in 0..Max range") { // Memory usage
            override fun createEntries(): TimeLineEntries<*> = TimeLineMinMaxEntries()
        },
        State(description = "Shows values change from given states") { // User switch
            override fun createEntries(): TimeLineEntries<*> = TimeLineStateEntries()
        },

        SingleState(description = "Shows states changes for single value") { // User switch
            override fun createEntries(): TimeLineEntries<*> = TimeLineSingleStateEntries()
        },

        Duration(description = "Shows duration bars") { // User switch
            override fun createEntries(): TimeLineEntries<*> = TimeLineDurationEntries()
        },

        Events(description = "Shows events that occur over the time")  {
            override fun createEntries(): TimeLineEntries<*> = TimeLineEventEntries()
        },
        ;

        abstract fun createEntries(): TimeLineEntries<*>

        enum class PercentageParams(val param: ExtractorParam) {
            KEY(ExtractorParam("key", "key value of the entry")),
            VALUE(ExtractorParam("value", "value of the entry")),
        }

        enum class MinMaxParams(val param: ExtractorParam) {
            KEY(ExtractorParam("key", "key value of the entry")),
            VALUE(ExtractorParam("value", "value of the entry")),
        }

        enum class StateParams(val param: ExtractorParam) {
            KEY(ExtractorParam("key", "key value of the entry")),
            VALUE(ExtractorParam("value", "New state value", required = true)),
            OLD_VALUE(ExtractorParam("oldvalue", "Previous state value", required = true)),
        }

        enum class SingleStateParams(val param: ExtractorParam) {
            KEY(ExtractorParam("key", "key value of the entry")),
            VALUE(ExtractorParam("value", "New state value", required = true)),
        }

        enum class DurationParams(val param: ExtractorParam) {
            KEY(ExtractorParam("key", "key value of the entry")),
            BEGIN(ExtractorParam("begin", "No description yet")),
            END(ExtractorParam("begin", "No description yet")),
        }

        enum class EventParams(val param: ExtractorParam) {
            KEY(ExtractorParam("key", "key value of the entry")),
            VALUE(ExtractorParam("value", "Event value", required = true)),
        }

    }

    enum class ExtractorType {
        KeyValueNamed,
        KeyValueGroups,
    }

    companion object {
        val Empty = TimelineFilter(
            name = "",
            enabled = false,
            filters = emptyMap(),
            extractPattern = null,
            diagramType = DiagramType.Events,
            extractorType = ExtractorType.KeyValueGroups
        )

        // TODO: merge duplicated code from ColorFilter
        fun assessFilter(filter: TimelineFilter, message: DLTMessage): Boolean {
            return with(filter) {
                filters.all {
                    val criteria = it.value
                    return@all enabled && when (it.key) {
                        FilterParameter.MessageType -> {
                            checkTextCriteria(
                                criteria,
                                message.extendedHeader?.messageInfo?.messageType?.name
                            )
                        }

                        FilterParameter.MessageTypeInfo -> {
                            checkTextCriteria(
                                criteria,
                                message.extendedHeader?.messageInfo?.messageTypeInfo?.name
                            )
                        }

                        FilterParameter.EcuId -> {
                            checkTextCriteria(criteria, message.standardHeader.ecuId)
                        }

                        FilterParameter.ContextId -> {
                            checkTextCriteria(criteria, message.extendedHeader?.contextId)
                        }

                        FilterParameter.AppId -> {
                            checkTextCriteria(criteria, message.extendedHeader?.applicationId)
                        }

                        FilterParameter.SessionId -> {
                            message.standardHeader.sessionId == criteria.value.toInt()
                        }

                        FilterParameter.Payload -> true
                    }
                }
            }
        }
    }
}
