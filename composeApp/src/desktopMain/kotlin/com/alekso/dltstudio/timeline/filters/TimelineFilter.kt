package com.alekso.dltstudio.timeline.filters

import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltstudio.logs.filtering.FilterCriteria
import com.alekso.dltstudio.logs.filtering.FilterParameter
import com.alekso.dltstudio.logs.filtering.TextCriteria
import com.alekso.dltstudio.logs.filtering.checkTextCriteria
import com.alekso.dltstudio.timeline.TimeLineEntries
import com.alekso.dltstudio.timeline.TimeLineEntry
import com.alekso.dltstudio.timeline.TimeLineEvent
import com.alekso.dltstudio.timeline.TimeLineEventEntries
import com.alekso.dltstudio.timeline.TimeLineEventEntry
import com.alekso.dltstudio.timeline.TimeLineMinMaxEntries
import com.alekso.dltstudio.timeline.TimeLinePercentageEntries
import com.alekso.dltstudio.timeline.TimeLineStateEntries
import com.alekso.dltstudio.timeline.TimeLineStateEntry


data class TimelineFilter(
    val name: String,
    val enabled: Boolean = true,
    val filters: Map<FilterParameter, FilterCriteria>,
    val extractPattern: String? = null, // regex only
    val diagramType: DiagramType,
    val extractorType: ExtractorType,
    val testClause: String? = null,
    ) {
    enum class AnalyzerType {
        Regex,
        IndexOf
    }

    enum class DiagramType(val description: String) {


        Percentage(description = "Shows values change from in 0..100% range") { // CPU Usage

            override fun extractEntry(
                regex: Regex,
                payload: String,
                entries: TimeLineEntries<*>,
                message: DLTMessage,
                filter: TimelineFilter
            ) {
                val matches = regex.find(payload)!!

                when (filter.extractorType) {
                    ExtractorType.KeyValueNamed -> {
                        val key: String = matches.groups["key"]?.value ?: "key"
                        val value: String? = matches.groups["value"]?.value
                        if (value != null) {
                            (entries as TimeLinePercentageEntries).addEntry(
                                TimeLineEntry(
                                    message.timeStampNano,
                                    key,
                                    value.toFloat()
                                )
                            )
                        }
                    }

                    ExtractorType.KeyValueGroups -> {
                        if (matches.groups.size > 2) {
                            for (i in 1..<matches.groups.size step 2) {
                                val key = matches.groups[i]?.value
                                val value = matches.groups[i + 1]?.value
                                if (key != null && value != null) {
                                    (entries as TimeLinePercentageEntries).addEntry(
                                        TimeLineEntry(
                                            message.timeStampNano,
                                            key,
                                            value.toFloat()
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            override fun createEntries(): TimeLineEntries<*> = TimeLinePercentageEntries()
        },
        MinMaxValue(description = "Shows values change from in 0..Max range") { // Memory usage
            override fun extractEntry(
                regex: Regex,
                payload: String,
                entries: TimeLineEntries<*>,
                message: DLTMessage,
                filter: TimelineFilter
            ) {
                val matches = regex.find(payload)!!

                when (filter.extractorType) {
                    ExtractorType.KeyValueNamed -> {
                        val key: String = matches.groups["key"]?.value ?: "key"
                        val value: String? = matches.groups["value"]?.value
                        if (value != null) {
                            (entries as TimeLineMinMaxEntries).addEntry(
                                TimeLineEntry(
                                    message.timeStampNano,
                                    key,
                                    value.toFloat()
                                )
                            )
                        }
                    }

                    ExtractorType.KeyValueGroups -> {
                        if (matches.groups.size > 2) {
                            for (i in 1..<matches.groups.size step 2) {
                                val key = matches.groups[i]?.value
                                val value = matches.groups[i + 1]?.value
                                if (key != null && value != null) {
                                    (entries as TimeLineMinMaxEntries).addEntry(
                                        TimeLineEntry(
                                            message.timeStampNano,
                                            key,
                                            value.toFloat()
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            override fun createEntries(): TimeLineEntries<*> = TimeLineMinMaxEntries()
        },
        State(description = "Shows values change from given states") { // User switch
            override fun extractEntry(
                regex: Regex,
                payload: String,
                entries: TimeLineEntries<*>,
                message: DLTMessage,
                filter: TimelineFilter
            ) {
                val matches = regex.find(payload)!!

                when (filter.extractorType) {
                    ExtractorType.KeyValueNamed -> {
                        val key: String = matches.groups["key"]?.value ?: "key"
                        val value: String? = matches.groups["value"]?.value
                        val oldValue: String? = matches.groups["oldvalue"]?.value
                        if (value != null && oldValue != null) {
                            (entries as TimeLineStateEntries).addEntry(
                                TimeLineStateEntry(
                                    message.timeStampNano,
                                    key,
                                    Pair(value, oldValue)
                                )
                            )
                        }
                    }

                    ExtractorType.KeyValueGroups -> {
                        if (matches.groups.size > 2) {
                            for (i in 1..3) {
                                val key = matches.groups[i]?.value
                                val value = matches.groups[i + 1]?.value
                                val oldValue = matches.groups[i + 2]?.value
                                if (key != null && value != null && oldValue != null) {
                                    (entries as TimeLineStateEntries).addEntry(
                                        TimeLineStateEntry(
                                            message.timeStampNano,
                                            key,
                                            Pair(value, oldValue)
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            override fun createEntries(): TimeLineEntries<*> = TimeLineStateEntries()
        },
        Events(description = "Shows events that occur over the time")  {
            override fun extractEntry(
                regex: Regex,
                payload: String,
                entries: TimeLineEntries<*>,
                message: DLTMessage,
                filter: TimelineFilter
            ) {
                val matches = regex.find(payload)!!

                when (filter.extractorType) {
                    ExtractorType.KeyValueNamed -> {
                        val key: String = matches.groups["key"]?.value ?: "key"
                        val value: String? = matches.groups["value"]?.value
                        val info: String? = matches.groups["info"]?.value
                        if (value != null && info != null) {
                            (entries as TimeLineEventEntries).addEntry(
                                TimeLineEventEntry(
                                    message.timeStampNano,
                                    key,
                                    TimeLineEvent(value, info)
                                )
                            )
                        }
                    }

                    ExtractorType.KeyValueGroups -> {
//                        if (matches.groups.size > 2) {
//                            for (i in 1..3) {
//                                val key = matches.groups[i]?.value
//                                val value = matches.groups[i + 1]?.value
//                                val oldValue = matches.groups[i + 2]?.value
//                                if (key != null && value != null && oldValue != null) {
//                                    (entries as TimeLineStateEntries).addEntry(
//                                        TimeLineStateEntry(
//                                            message.timeStampNano,
//                                            key,
//                                            Pair(value, oldValue)
//                                        )
//                                    )
//                                }
//                            }
//                        }
                    }
                }
            }

            override fun createEntries(): TimeLineEntries<*> = TimeLineEventEntries()
        }, // Crash events
        ;

        abstract fun extractEntry(
            regex: Regex,
            payload: String,
            entries: TimeLineEntries<*>,
            message: DLTMessage,
            filter: TimelineFilter
        )

        abstract fun createEntries(): TimeLineEntries<*>
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
