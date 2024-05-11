package com.alekso.dltstudio.timeline.filters

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.rememberDialogState
import com.alekso.dltparser.dlt.MessageInfo
import com.alekso.dltstudio.logs.colorfilters.FilterCriteria
import com.alekso.dltstudio.logs.colorfilters.FilterParameter
import com.alekso.dltstudio.logs.colorfilters.TextCriteria
import com.alekso.dltstudio.ui.CustomButton
import com.alekso.dltstudio.ui.CustomDropDown
import com.alekso.dltstudio.ui.CustomEditText


class EditTimelineFilterDialogState(
    var visible: Boolean,
    var filter: TimelineFilter = TimelineFilter.Empty,
    val filterIndex: Int = -1
)

@Composable
fun EditTimelineFilterDialog(
    visible: Boolean,
    onDialogClosed: () -> Unit,
    colorFilter: TimelineFilter,
    onFilterUpdate: (Int, TimelineFilter) -> Unit,
    colorFilterIndex: Int,
    testPayloadText: String = "",
) {
    DialogWindow(
        visible = visible, onCloseRequest = onDialogClosed,
        title = if (colorFilterIndex >= 0) "Edit Timeline Filter" else "Add new Timeline filter",
        state = rememberDialogState(width = 700.dp, height = 500.dp)
    ) {
        EditTimelineFilterPanel(
            colorFilter,
            colorFilterIndex,
            onFilterUpdate,
            onDialogClosed,
            testPayloadText
        )
    }
}

private val COL_NAME_SIZE_DP = 150.dp
private val COL_VALUE = 250.dp
private val COL_PATTERN = 400.dp

@Composable
fun EditTimelineFilterPanel(
    filter: TimelineFilter,
    filterIndex: Int,
    onFilterUpdate: (Int, TimelineFilter) -> Unit,
    onDialogClosed: () -> Unit,
    testPayloadText: String = "",
) {
    var filterName by rememberSaveable { mutableStateOf(filter.name) }
    var diagramType by rememberSaveable { mutableStateOf(filter.diagramType.name) }
    var messageType by rememberSaveable { mutableStateOf(filter.filters[FilterParameter.MessageType]?.value) }
    var messageTypeInfo by rememberSaveable { mutableStateOf(filter.filters[FilterParameter.MessageTypeInfo]?.value) }
    var ecuId by rememberSaveable { mutableStateOf(filter.filters[FilterParameter.EcuId]?.value) }
    var appId by rememberSaveable { mutableStateOf(filter.filters[FilterParameter.AppId]?.value) }
    var contextId by rememberSaveable { mutableStateOf(filter.filters[FilterParameter.ContextId]?.value) }
    var sessionId by rememberSaveable { mutableStateOf(filter.filters[FilterParameter.SessionId]?.value) }
    var extractPattern by rememberSaveable { mutableStateOf(filter.extractPattern) }
    var extractorType by rememberSaveable { mutableStateOf(filter.extractorType.name) }
    val colNameStyle = Modifier.width(COL_NAME_SIZE_DP).padding(horizontal = 4.dp)
    var testPayload by rememberSaveable { mutableStateOf(testPayloadText) }
    var groupsTestValue by rememberSaveable { mutableStateOf("") }

    Column(
        Modifier.width(1000.dp).padding(4.dp),

    ) {
        Column(
            modifier = Modifier.weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(modifier = colNameStyle, text = "Name")
                CustomEditText(
                    modifier = Modifier.width(COL_VALUE),
                    value = filterName, onValueChange = {
                        filterName = it
                    }
                )
            }

            Row {
                val items = mutableListOf<String>()
                items.addAll(TimelineFilter.DiagramType.entries.map { it.name })
                var initialSelection = items.indexOfFirst { it == filter.diagramType.name }
                if (initialSelection == -1) initialSelection = 0

                Text(modifier = colNameStyle, text = "Diagram Type")
                CustomDropDown(
                    modifier = Modifier.width(COL_VALUE).padding(horizontal = 4.dp),
                    items = items,
                    initialSelectedIndex = initialSelection,
                    onItemsSelected = { i -> diagramType = items[i] }
                )
            }

            Row {
                Text(
                    modifier = Modifier.width(COL_PATTERN)
                        .offset(x = COL_NAME_SIZE_DP)
                        .padding(horizontal = 4.dp),
                    text = TimelineFilter.DiagramType.entries.first { it.name == diagramType }.description
                )
            }

            Row {
                val items = mutableListOf("Any")
                items.addAll(MessageInfo.MessageType.entries.map { it.name })
                var initialSelection =
                    items.indexOfFirst { it == filter.filters[FilterParameter.MessageType]?.value }
                if (initialSelection == -1) initialSelection = 0

                Text(modifier = colNameStyle, text = "Message Type")
                CustomDropDown(
                    modifier = Modifier.width(COL_VALUE).padding(horizontal = 4.dp),
                    items = items,
                    initialSelectedIndex = initialSelection,
                    onItemsSelected = { i ->
                        messageType = if (i > 0) {
                            items[i]
                        } else null
                    }
                )
            }

            Row {
                val items = mutableListOf("Any")
                items.addAll(MessageInfo.MessageTypeInfo.entries.map { it.name })
                var initialSelection =
                    items.indexOfFirst { it == filter.filters[FilterParameter.MessageTypeInfo]?.value }
                if (initialSelection == -1) initialSelection = 0

                Text(modifier = colNameStyle, text = "Message Type Info")
                CustomDropDown(
                    modifier = Modifier.width(COL_VALUE).padding(horizontal = 4.dp),
                    items = items,
                    initialSelectedIndex = initialSelection,
                    onItemsSelected = { i ->
                        messageTypeInfo = if (i > 0) {
                            items[i]
                        } else null
                    }
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(modifier = colNameStyle, text = "ECU ID")
                CustomEditText(
                    modifier = Modifier.width(COL_VALUE),
                    value = ecuId ?: "", onValueChange = {
                        ecuId = it
                    }
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(modifier = colNameStyle, text = "App ID")
                CustomEditText(
                    modifier = Modifier.width(COL_VALUE),
                    value = appId ?: "", onValueChange = {
                        appId = it
                    }
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(modifier = colNameStyle, text = "Context ID")
                CustomEditText(
                    modifier = Modifier.width(COL_VALUE),
                    value = contextId ?: "", onValueChange = {
                        contextId = it
                    }
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(modifier = colNameStyle, text = "Session ID")
                CustomEditText(
                    modifier = Modifier.width(COL_VALUE),
                    value = sessionId ?: "", onValueChange = {
                        sessionId = it
                    }
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(modifier = colNameStyle, text = "Extract pattern")
            }
            Row {
                CustomEditText(
                    modifier = Modifier.wrapContentHeight(Alignment.Top).fillMaxWidth()
                        .align(Alignment.Top),
                    singleLine = false,
                    value = extractPattern ?: "", onValueChange = {
                        extractPattern = it
                        groupsTestValue = testRegex(extractPattern, testPayload)
                    }
                )
            }


            Row {
                val items = mutableListOf<String>()
                items.addAll(TimelineFilter.ExtractorType.entries.map { it.name })
                var initialSelection = items.indexOfFirst { it == filter.extractorType.name }
                if (initialSelection == -1) initialSelection = 0

                Text(modifier = colNameStyle, text = "Extractor type")
                CustomDropDown(
                    modifier = Modifier.width(COL_VALUE).padding(horizontal = 4.dp),
                    items = items,
                    initialSelectedIndex = initialSelection,
                    onItemsSelected = { i -> extractorType = items[i] }
                )
            }

            // Regex check
            Divider(modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(modifier = colNameStyle, text = "Regex check")
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                CustomEditText(
                    modifier = Modifier.fillMaxWidth().height(44.dp).align(Alignment.Top),
                    singleLine = false,
                    value = testPayload,
                    onValueChange = {
                        testPayload = it
                    }
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(modifier = colNameStyle, text = "Groups:")
                Text(
                    modifier = Modifier.fillMaxWidth().wrapContentHeight(Alignment.Top)
                        .padding(horizontal = 4.dp),
                    text = groupsTestValue
                )
            }
        }

        CustomButton(onClick = {
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
                    diagramType = TimelineFilter.DiagramType.valueOf(diagramType),
                    extractorType = TimelineFilter.ExtractorType.valueOf(extractorType),
                )
            )
            onDialogClosed()
        }) {
            Text(text = if (filterIndex >= 0) "Update" else "Add")
        }

    }
}

fun testRegex(extractPattern: String?, testPayload: String, global: Boolean = false): String {
    var groupsTestValue = ""
    if (extractPattern != null) {
        try {
            if (global) {
                val matches = Regex(extractPattern).findAll(testPayload)
                groupsTestValue = matches.map { "${it.groups[1]?.value} -> ${it.groups[2]?.value}" }
                    .joinToString("\n")
                println("Groups: '$groupsTestValue'")
            } else {
                val matches = Regex(extractPattern).find(testPayload)
                if (matches?.groups == null) {
                    groupsTestValue = "Empty groups"
                } else {

                    val matchesText = StringBuilder()
                    matches.groups.forEachIndexed { index, group ->
                        if (index > 0 && group != null) {
                            matchesText.append(group.value)
                            if (index < matches.groups.size - 1) {
                                if (index % 2 == 1) {
                                    matchesText.append(" -> ")
                                } else {
                                    matchesText.append("\n")
                                }
                            }
                        }
                    }
                    groupsTestValue = matchesText.toString()
                    println("Groups: '$groupsTestValue'")
                }
            }
        } catch (e: Exception) {
            groupsTestValue = "Invalid regex ${e.printStackTrace()}"
        }
    }
    return groupsTestValue
}

@Preview
@Composable
fun PreviewEditTimelineFilterDialog() {
    val filter = TimelineFilter(
        name = "CPU Usage by PID", enabled = true,
        filters = mutableMapOf(),
        extractPattern = """(cpu\d+?):\s?(\d+(?>.\d+)?)%\s?(cpu\d+?):\s?(\d+(?>.\d+)?)%\s?(cpu\d+?):\s?(\d+(?>.\d+)?)%\s?(cpu\d+?):\s?(\d+(?>.\d+)?)%\s?(cpu\d+?):\s?(\d+(?>.\d+)?)%\s?(cpu\d+?):\s?(\d+(?>.\d+)?)%\s?(cpu\d+?):\s?(\d+(?>.\d+)?)%\s?(cpu\d+?):\s?(\d+(?>.\d+)?)%\s?""",
        diagramType = TimelineFilter.DiagramType.Percentage,
        extractorType = TimelineFilter.ExtractorType.KeyValueNamed
    )

    Column(Modifier.background(Color(238, 238, 238))) {
        EditTimelineFilterPanel(
            filter,
            0,
            { _, _ -> },
            {},
            "cpu0: 36.9% cpu1: 40.4% cpu2: 40% cpu3: 43.5% cpu4: 45.3% cpu5: 27.9% cpu6: 16.8% cpu7: 14.1%"
        )
    }
}