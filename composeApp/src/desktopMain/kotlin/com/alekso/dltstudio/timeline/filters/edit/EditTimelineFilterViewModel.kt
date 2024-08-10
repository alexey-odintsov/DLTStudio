package com.alekso.dltstudio.timeline.filters.edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.alekso.dltparser.dlt.extendedheader.MessageType
import com.alekso.dltstudio.logs.filtering.FilterCriteria
import com.alekso.dltstudio.logs.filtering.FilterParameter
import com.alekso.dltstudio.logs.filtering.TextCriteria
import com.alekso.dltstudio.timeline.DiagramType
import com.alekso.dltstudio.timeline.filters.TimelineFilter

class EditTimelineFilterViewModel(
    val filterIndex: Int,
    val filter: TimelineFilter,
    val onFilterUpdate: (Int, TimelineFilter) -> Unit,
    val onDialogClosed: () -> Unit
) {
    var tabIndex by mutableStateOf(0)

    var filterName by mutableStateOf(filter.name)
    var diagramType by mutableStateOf(filter.diagramType.name)
    var messageType by mutableStateOf(filter.filters[FilterParameter.MessageType]?.value)
    var messageTypeInfo by mutableStateOf(filter.filters[FilterParameter.MessageTypeInfo]?.value)
    var ecuId by mutableStateOf(filter.filters[FilterParameter.EcuId]?.value)
    var appId by mutableStateOf(filter.filters[FilterParameter.AppId]?.value)
    var contextId by mutableStateOf(filter.filters[FilterParameter.ContextId]?.value)
    var sessionId by mutableStateOf(filter.filters[FilterParameter.SessionId]?.value)
    var extractPattern by mutableStateOf(filter.extractPattern)
    var extractorType by mutableStateOf(filter.extractorType.name)
    var testPayload by mutableStateOf(filter.testClause)
    var groupsTestValue by mutableStateOf("")

    val messageTypeItems = mutableListOf("Any") // todo: Rename to messageTypeItems
    var initialSelection by mutableStateOf(messageTypeItems.indexOfFirst { it == filter.filters[FilterParameter.MessageType]?.value })

    init {
        messageTypeItems.addAll(MessageType.entries.map { it.name })
        if (initialSelection == -1) initialSelection = 0

    }

    fun onMessageTypeChanged(i: Int) {
        messageType = if (i > 0) {
            messageTypeItems[i]
        } else null
    }

    fun onUpdateClicked() {
        val map = mutableMapOf<FilterParameter, FilterCriteria>()
        messageType?.let {
            map[FilterParameter.MessageType] = FilterCriteria(it, TextCriteria.PlainText)
        }
        messageTypeInfo?.let {
            map[FilterParameter.MessageTypeInfo] = FilterCriteria(it, TextCriteria.PlainText)
        }
        ecuId?.let {
            map[FilterParameter.EcuId] = FilterCriteria(it, TextCriteria.PlainText)
        }
        appId?.let {
            map[FilterParameter.AppId] = FilterCriteria(it, TextCriteria.PlainText)
        }
        contextId?.let {
            map[FilterParameter.ContextId] = FilterCriteria(it, TextCriteria.PlainText)
        }
        sessionId?.let {
            map[FilterParameter.SessionId] = FilterCriteria(it, TextCriteria.PlainText)
        }
        onFilterUpdate(
            filterIndex,
            TimelineFilter(
                name = filterName,
                filters = map,
                extractPattern = extractPattern,
                diagramType = DiagramType.valueOf(diagramType),
                extractorType = TimelineFilter.ExtractorType.valueOf(extractorType),
                testClause = testPayload,
            )
        )
        onDialogClosed()
    }

}