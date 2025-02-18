package com.alekso.dltstudio.timeline.filters.edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.alekso.dltmessage.extendedheader.MessageType
import com.alekso.dltmessage.extendedheader.MessageTypeInfo
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
    var extractorType by mutableStateOf(filter.extractorType)
    var testPayload by mutableStateOf(filter.testClause)
    var groupsTestValue by mutableStateOf("")

    val messageTypeItems = mutableStateListOf("Any")
    var messageTypeSelectionIndex by mutableStateOf(0)

    val messageTypeInfoItems = mutableStateListOf("Any")
    var messageTypeInfoSelectionIndex by mutableStateOf(0)

    val diagramTypeItems = mutableStateListOf<String>()
    var diagramTypeSelectionIndex by mutableStateOf(0)



    init {
        messageTypeItems.addAll(MessageType.entries.map { it.name })
        messageTypeSelectionIndex = messageTypeItems.indexOfFirst { it == filter.filters[FilterParameter.MessageType]?.value }
        if (messageTypeSelectionIndex == -1) messageTypeSelectionIndex = 0

        messageTypeInfoItems.addAll(MessageTypeInfo.entries.map { it.name })
        messageTypeInfoSelectionIndex = messageTypeInfoItems.indexOfFirst { it == filter.filters[FilterParameter.MessageTypeInfo]?.value }
        if (messageTypeInfoSelectionIndex == -1) messageTypeInfoSelectionIndex = 0

        diagramTypeItems.addAll(DiagramType.entries.map { it.name })
        diagramTypeSelectionIndex = diagramTypeItems.indexOfFirst { it == filter.diagramType.name }
        if (diagramTypeSelectionIndex == -1) diagramTypeSelectionIndex = 0

    }

    fun onMessageTypeChanged(i: Int) {
        messageTypeSelectionIndex = i
        messageType = if (i > 0) {
            messageTypeItems[i]
        } else null
    }

    fun onMessageTypeInfoChanged(i: Int) {
        messageTypeInfoSelectionIndex = i
        messageTypeInfo = if (i > 0) {
            messageTypeInfoItems[i]
        } else null
    }

    fun onDiagramTypeSelected(i: Int) {
        diagramTypeSelectionIndex = i
        diagramType = diagramTypeItems[i]
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
                extractorType = extractorType,
                testClause = testPayload,
            )
        )
        onDialogClosed()
    }

}