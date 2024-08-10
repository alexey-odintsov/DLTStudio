package com.alekso.dltstudio.timeline.filters.edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.alekso.dltstudio.logs.filtering.FilterParameter
import com.alekso.dltstudio.timeline.filters.TimelineFilter

class EditTimelineFilterViewModel(val filter: TimelineFilter) {

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

}