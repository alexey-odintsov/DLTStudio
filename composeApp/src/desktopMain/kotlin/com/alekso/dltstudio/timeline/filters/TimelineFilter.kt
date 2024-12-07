package com.alekso.dltstudio.timeline.filters

import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltstudio.logs.filtering.FilterCriteria
import com.alekso.dltstudio.logs.filtering.FilterParameter
import com.alekso.dltstudio.logs.filtering.checkTextCriteria
import com.alekso.dltstudio.timeline.DiagramType
import com.alekso.dltstudio.timeline.filters.extractors.EntriesExtractor.ExtractionType

const val NO_KEY = "no_key"


data class TimelineFilter(
    val name: String,
    val enabled: Boolean = true,
    val filters: Map<FilterParameter, FilterCriteria>,
    val extractPattern: String? = null, // regex only
    val diagramType: DiagramType,
    val extractorType: ExtractionType,
    val testClause: String? = null,
    ) {

    val key: String = "$filters$extractPattern".hashCode().toString()

    companion object {
        val Empty = TimelineFilter(
            name = "",
            enabled = false,
            filters = emptyMap(),
            extractPattern = null,
            diagramType = DiagramType.Events,
            extractorType = ExtractionType.KeyValueNamed
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
