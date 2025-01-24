package com.alekso.dltstudio.timeline.filters.edit

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.alekso.dltstudio.timeline.DiagramType
import com.alekso.dltstudio.timeline.filters.ExtractorChecker
import com.alekso.dltstudio.timeline.filters.TimelineFilter
import com.alekso.dltstudio.timeline.filters.extractors.EntriesExtractor
import com.alekso.dltstudio.timeline.graph.TimelinePreviewFactory
import com.alekso.dltstudio.ui.CustomDropDown
import com.alekso.dltstudio.ui.CustomEditText

private val COL_VALUE = 250.dp
private val COL_PATTERN = 400.dp
private val colNameStyle = Modifier.padding(horizontal = 4.dp)
private val paragraph = Modifier.padding(top = 4.dp)

@Composable
fun EditTimelineFilterExtractPanel(viewModel: EditTimelineFilterViewModel) {
    Row {
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(modifier = colNameStyle, text = "Diagram Type")
                CustomDropDown(
                    modifier = Modifier.width(COL_VALUE).padding(horizontal = 4.dp),
                    items = viewModel.diagramTypeItems,
                    initialSelectedIndex = viewModel.diagramTypeSelectionIndex,
                    onItemsSelected = viewModel::onDiagramTypeSelected
                )
            }

            Text(
                modifier = Modifier.width(COL_PATTERN).padding(horizontal = 4.dp).then(paragraph),
                text = DiagramType.entries.first { it.name == viewModel.diagramType }.description
            )

            Row(modifier = paragraph, verticalAlignment = Alignment.CenterVertically) {
                val items = mutableListOf<String>()
                items.addAll(EntriesExtractor.ExtractionType.entries.map { it.name })
                var initialSelection =
                    items.indexOfFirst { it == viewModel.filter.extractorType.name }
                if (initialSelection == -1) initialSelection = 0

                Text(modifier = colNameStyle, text = "Extractor type")
                CustomDropDown(
                    modifier = Modifier.width(COL_VALUE).padding(horizontal = 4.dp),
                    items = items,
                    initialSelectedIndex = initialSelection,
                    onItemsSelected = { i ->
                        viewModel.extractorType = EntriesExtractor.ExtractionType.valueOf(items[i])
                    }
                )
            }

            Text(modifier = colNameStyle.then(paragraph), text = viewModel.extractorType.description)

            when (viewModel.extractorType) {
                EntriesExtractor.ExtractionType.NamedGroupsOneEntry -> {
                    Row(modifier = paragraph, verticalAlignment = Alignment.CenterVertically) {
                        Text(modifier = colNameStyle, text = "Extract parameters")
                    }
                    Column {
                        DiagramType.entries.first { it.name == viewModel.diagramType }.params.forEach { (p, param) ->
                            Text(
                                modifier = Modifier.fillMaxWidth().padding(start = 14.dp, end = 4.dp),
                                text = "(?<${param.key}>.*) – Required: ${param.required}. ${param.description}"
                            )
                        }
                    }

                }
                EntriesExtractor.ExtractionType.NamedGroupsManyEntries -> {

                }
                EntriesExtractor.ExtractionType.GroupsManyEntries -> {

                }
            }
        }
        Column(Modifier.width(250.dp)) {
            TimelinePreviewFactory.getPreview(
                DiagramType.valueOf(viewModel.diagramType),
                Modifier.height(200.dp).fillMaxWidth()
            )
        }
    }

    Column {
        Text(modifier = colNameStyle, text = "Extraction regexp:")
        CustomEditText(
            modifier = Modifier.wrapContentHeight(Alignment.Top).fillMaxWidth(),
            singleLine = false,
            value = viewModel.extractPattern ?: "", onValueChange = {
                viewModel.extractPattern = it
                viewModel.groupsTestValue = ExtractorChecker.testRegex(
                    extractPattern = viewModel.extractPattern,
                    testPayload = viewModel.testPayload,
                    diagramType = DiagramType.valueOf(viewModel.diagramType),
                    extractorType = viewModel.extractorType,
                )
            }
        )
    }

    // Regex check
    Divider(modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(modifier = colNameStyle, text = "Regex check (Put your payload to test)")
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
    Text(modifier = colNameStyle, text = "Groups:")
    Text(
        modifier = Modifier.fillMaxWidth().wrapContentHeight(Alignment.Top)
            .padding(horizontal = 4.dp),
        text = viewModel.groupsTestValue
    )
}

@Preview
@Composable
fun PreviewEditTimelineFilterExtractPanel() {
    val filter = TimelineFilter(
        name = "CPU Usage by PID", enabled = true,
        filters = mutableMapOf(),
        extractPattern = """(cpu\d+?):\s?(\d+(?>.\d+)?)%\s?(cpu\d+?):\s?(\d+(?>.\d+)?)%\s?(cpu\d+?):\s?(\d+(?>.\d+)?)%\s?(cpu\d+?):\s?(\d+(?>.\d+)?)%\s?(cpu\d+?):\s?(\d+(?>.\d+)?)%\s?(cpu\d+?):\s?(\d+(?>.\d+)?)%\s?(cpu\d+?):\s?(\d+(?>.\d+)?)%\s?(cpu\d+?):\s?(\d+(?>.\d+)?)%\s?""",
        diagramType = DiagramType.Percentage,
        extractorType = EntriesExtractor.ExtractionType.NamedGroupsManyEntries,
        testClause = "cpu0: 36.9% cpu1: 40.4% cpu2: 40% cpu3: 43.5% cpu4: 45.3% cpu5: 27.9% cpu6: 16.8% cpu7: 14.1%",
    )

    Column(Modifier.background(Color(238, 238, 238))) {
        EditTimelineFilterExtractPanel(
            EditTimelineFilterViewModel(0, filter, { i, f -> }, {})
        )
    }
}