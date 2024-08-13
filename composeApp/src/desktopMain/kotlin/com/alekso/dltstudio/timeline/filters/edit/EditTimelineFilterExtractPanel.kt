package com.alekso.dltstudio.timeline.filters.edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alekso.dltstudio.timeline.DiagramType
import com.alekso.dltstudio.timeline.filters.ExtractorChecker
import com.alekso.dltstudio.timeline.filters.TimelineFilter
import com.alekso.dltstudio.ui.CustomDropDown
import com.alekso.dltstudio.ui.CustomEditText

private val COL_NAME_SIZE_DP = 150.dp
private val COL_VALUE = 250.dp
private val COL_PATTERN = 400.dp
private val colNameStyle = Modifier.padding(horizontal = 4.dp)

@Composable
fun EditTimelineFilterExtractPanel(viewModel: EditTimelineFilterViewModel) {
    Row {
        Text(modifier = colNameStyle, text = "Diagram Type")
        CustomDropDown(
            modifier = Modifier.width(COL_VALUE).padding(horizontal = 4.dp),
            items = viewModel.diagramTypeItems,
            initialSelectedIndex = viewModel.diagramTypeSelectionIndex,
            onItemsSelected = viewModel::onDiagramTypeSelected
        )
    }

    Row {
        Text(
            modifier = Modifier.width(COL_PATTERN)
                .offset(x = COL_NAME_SIZE_DP)
                .padding(horizontal = 4.dp),
            text = DiagramType.entries.first { it.name == viewModel.diagramType }.description
        )
    }

    Row {
        val items = mutableListOf<String>()
        items.addAll(TimelineFilter.ExtractorType.entries.map { it.name })
        var initialSelection = items.indexOfFirst { it == viewModel.filter.extractorType.name }
        if (initialSelection == -1) initialSelection = 0

        Text(modifier = colNameStyle, text = "Extractor type")
        CustomDropDown(
            modifier = Modifier.width(COL_VALUE).padding(horizontal = 4.dp),
            items = items,
            initialSelectedIndex = initialSelection,
            onItemsSelected = { i -> viewModel.extractorType = items[i] }
        )
    }


    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(modifier = colNameStyle, text = "Extract parameters")
    }
    Column() {
        DiagramType.entries.first { it.name == viewModel.diagramType }.params.forEach { (p, param) ->
            Text(
                modifier = Modifier.fillMaxWidth().padding(start = 14.dp, end = 4.dp),
                text = "(?<${param.key}>.*) – Required: ${param.required}. ${param.description}"
            )
        }
    }


    Row {
        CustomEditText(
            modifier = Modifier.wrapContentHeight(Alignment.Top).fillMaxWidth()
                .align(Alignment.Top),
            singleLine = false,
            value = viewModel.extractPattern ?: "", onValueChange = {
                viewModel.extractPattern = it
                viewModel.groupsTestValue = ExtractorChecker.testRegex(
                    extractPattern = viewModel.extractPattern,
                    testPayload = viewModel.testPayload,
                    diagramType = DiagramType.valueOf(viewModel.diagramType),
                    extractorType = TimelineFilter.ExtractorType.valueOf(viewModel.extractorType),
                )
            }
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
            value = viewModel.testPayload ?: "",
            onValueChange = {
                viewModel.testPayload = it
            }
        )
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(modifier = colNameStyle, text = "Groups:")
        Text(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(Alignment.Top)
                .padding(horizontal = 4.dp),
            text = viewModel.groupsTestValue
        )
    }
}