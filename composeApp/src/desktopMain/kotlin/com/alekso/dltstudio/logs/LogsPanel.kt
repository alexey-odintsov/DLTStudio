package com.alekso.dltstudio.logs

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltparser.dlt.SampleData
import com.alekso.dltstudio.ParseSession
import com.alekso.dltstudio.ui.HorizontalDivider
import java.io.File

@Composable
fun LogsPanel(
    modifier: Modifier = Modifier,
    searchText: String,
    dltSession: ParseSession?,
    colorFilters: List<CellColorFilter> = emptyList(),
    logPreviewVisibility: Boolean,
    toolbarFatalChecked: Boolean,
    toolbarErrorChecked: Boolean,
    toolbarWarningChecked: Boolean,
    updateSearchText: (String) -> Unit,
    updateToolbarFatalCheck: (Boolean) -> Unit,
    updateToolbarErrorCheck: (Boolean) -> Unit,
    updateToolbarWarningCheck: (Boolean) -> Unit,
    updateToolbarLogPreviewCheck: (Boolean) -> Unit,
) {
    var selectedRow by remember { mutableStateOf(0) }
    val dltMessages = dltSession?.dltMessages ?: emptyList()
    val searchResult = mutableListOf<DLTMessage>()

    Column(modifier = modifier) {
        LogsToolbar(
            searchText,
            toolbarFatalChecked,
            toolbarErrorChecked,
            toolbarWarningChecked,
            logPreviewVisibility,
            updateSearchText,
            updateToolbarFatalCheck,
            updateToolbarErrorCheck,
            updateToolbarWarningCheck,
            updateToolbarLogPreviewCheck,
        )
        Divider()

        Row(modifier = modifier.fillMaxWidth()) {
            LazyScrollable(
                Modifier.weight(1f).fillMaxHeight().background(Color.LightGray),
                dltMessages,
                colorFilters,
                selectedRow = selectedRow,
            ) { i -> selectedRow = i }

            if (logPreviewVisibility) {
                HorizontalDivider()
                LogPreview(
                    Modifier.fillMaxHeight().width(300.dp),
                    dltSession?.dltMessages?.getOrNull(selectedRow),
                    selectedRow
                )
            }
        }

        Divider()
        LazyScrollable(
            Modifier.height(200.dp).fillMaxWidth().background(Color.LightGray),
            searchResult,
            colorFilters,
            selectedRow = selectedRow,
        ) { i -> selectedRow = i }

    }
}

@Preview
@Composable
fun PreviewLogsPanel() {
    val dltSession = ParseSession({}, listOf(File("")))
    dltSession.dltMessages.addAll(SampleData.getSampleDltMessages(20))

    LogsPanel(
        Modifier.fillMaxSize(),
        "Search text",
        dltSession = dltSession,
        logPreviewVisibility = true,
        toolbarFatalChecked = true,
        toolbarErrorChecked = true,
        toolbarWarningChecked = true,
        updateSearchText = { },
        updateToolbarFatalCheck = { },
        updateToolbarErrorCheck = { },
        updateToolbarWarningCheck = { },
        updateToolbarLogPreviewCheck = { }
    )
}