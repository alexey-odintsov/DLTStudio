package com.alekso.dltstudio.timeline.filters.edit

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alekso.dltparser.dlt.extendedheader.MessageType
import com.alekso.dltparser.dlt.extendedheader.MessageTypeInfo
import com.alekso.dltstudio.logs.filtering.FilterParameter
import com.alekso.dltstudio.ui.CustomDropDown
import com.alekso.dltstudio.ui.CustomEditText

private val COL_NAME_SIZE_DP = 150.dp
private val COL_VALUE = 250.dp
private val colNameStyle = Modifier.padding(horizontal = 4.dp)

@Composable
fun EditTimelineFilterFilterPanel(
    viewModel: EditTimelineFilterViewModel,
) {
    Row {
        val items = mutableListOf("Any")
        items.addAll(MessageType.entries.map { it.name })
        var initialSelection =
            items.indexOfFirst { it == viewModel.filter.filters[FilterParameter.MessageType]?.value }
        if (initialSelection == -1) initialSelection = 0

        Text(modifier = colNameStyle.width(COL_NAME_SIZE_DP), text = "Message Type")
        CustomDropDown(
            modifier = Modifier.width(COL_VALUE).padding(horizontal = 4.dp),
            items = items,
            initialSelectedIndex = initialSelection,
            onItemsSelected = { i ->
                viewModel.messageType = if (i > 0) {
                    items[i]
                } else null
            }
        )
    }

    Row {
        val items = mutableListOf("Any")
        items.addAll(MessageTypeInfo.entries.map { it.name })
        var initialSelection =
            items.indexOfFirst { it == viewModel.filter.filters[FilterParameter.MessageTypeInfo]?.value }
        if (initialSelection == -1) initialSelection = 0

        Text(modifier = colNameStyle.width(COL_NAME_SIZE_DP), text = "Message Type Info")
        CustomDropDown(
            modifier = Modifier.width(COL_VALUE).padding(horizontal = 4.dp),
            items = items,
            initialSelectedIndex = initialSelection,
            onItemsSelected = { i ->
                viewModel.messageTypeInfo = if (i > 0) {
                    items[i]
                } else null
            }
        )
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(modifier = colNameStyle.width(COL_NAME_SIZE_DP), text = "ECU ID")
        CustomEditText(
            modifier = Modifier.width(COL_VALUE),
            value = viewModel.ecuId ?: "", onValueChange = {
                viewModel.ecuId = it
            }
        )
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(modifier = colNameStyle.width(COL_NAME_SIZE_DP), text = "App ID")
        CustomEditText(
            modifier = Modifier.width(COL_VALUE),
            value = viewModel.appId ?: "", onValueChange = {
                viewModel.appId = it
            }
        )
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(modifier = colNameStyle.width(COL_NAME_SIZE_DP), text = "Context ID")
        CustomEditText(
            modifier = Modifier.width(COL_VALUE),
            value = viewModel.contextId ?: "", onValueChange = {
                viewModel.contextId = it
            }
        )
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(modifier = colNameStyle.width(COL_NAME_SIZE_DP), text = "Session ID")
        CustomEditText(
            modifier = Modifier.width(COL_VALUE),
            value = viewModel.sessionId ?: "", onValueChange = {
                viewModel.sessionId = it
            }
        )
    }
}