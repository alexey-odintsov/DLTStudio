package com.alekso.dltstudio.timeline.filters

import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltstudio.logs.colorfilters.FilterCriteria
import com.alekso.dltstudio.logs.colorfilters.FilterParameter
import com.alekso.dltstudio.logs.colorfilters.TextCriteria


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


        // TODO: merge duplicated code from ColorFilter
        private fun checkTextCriteria(criteria: FilterCriteria, message: String?) =
            when (criteria.textCriteria) {
                TextCriteria.PlainText -> message?.contains(criteria.value) ?: false
                TextCriteria.LowerCase -> message?.lowercase()?.contains(criteria.value) ?: false
                TextCriteria.Regex -> message?.contains(criteria.value.toRegex()) ?: false
            }

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
