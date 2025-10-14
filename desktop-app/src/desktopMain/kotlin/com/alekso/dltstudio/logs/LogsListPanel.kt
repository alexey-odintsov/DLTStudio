package com.alekso.dltstudio.logs

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import com.alekso.dltmessage.SampleData
import com.alekso.dltstudio.logs.colorfilters.ColorFilter
import com.alekso.dltstudio.model.ColumnParams
import com.alekso.dltstudio.model.contract.LogMessage
import com.alekso.dltstudio.ui.Panel

@Composable
fun LogsListPanel(
    modifier: Modifier = Modifier,
    columnParams: SnapshotStateList<ColumnParams>,
    messages: SnapshotStateList<LogMessage>,
    colorFilters: SnapshotStateList<ColorFilter>,
    selectedRow: Int,
    logsListState: LazyListState,
    onLogsRowSelected: (Int, Int) -> Unit,
    wrapContent: Boolean,
    rowContextMenuCallbacks: RowContextMenuCallbacks,
    columnsContextMenuCallbacks: ColumnsContextMenuCallbacks,
    showComments: Boolean,
    onColumnResized: (String, Float) -> Unit,
    markedIds: SnapshotStateList<Int>,
) {
    Panel(
        modifier = modifier,
        title = "Messages"
    ) {
        LazyScrollable(
            modifier = Modifier.fillMaxSize(),
            columnParams = columnParams,
            logMessages = messages,
            markedIds = markedIds,
            colorFilters = colorFilters,
            selectedRow = selectedRow,
            onRowSelected = onLogsRowSelected,
            listState = logsListState,
            wrapContent = wrapContent,
            rowContextMenuCallbacks = rowContextMenuCallbacks,
            columnsContextMenuCallbacks = columnsContextMenuCallbacks,
            showComments = showComments,
            onColumnResized = onColumnResized,
        )
    }
}

@Preview
@Composable
fun PreviewLogsListPanel() {
    val list = SnapshotStateList<LogMessage>()
    list.addAll(
        SampleData.getSampleDltMessages(20)
            .map { LogMessage(dltMessage = it, comment = "Test comment") })

    LogsListPanel(
        modifier = Modifier.fillMaxSize(),
        columnParams = mutableStateListOf(*ColumnParams.DefaultParams.toTypedArray()),
        messages = list,
        colorFilters = mutableStateListOf(),
        selectedRow = 1,
        logsListState = LazyListState(),
        onLogsRowSelected = { _, _ -> },
        wrapContent = true,
        rowContextMenuCallbacks = RowContextMenuCallbacks.Stub,
        columnsContextMenuCallbacks = ColumnsContextMenuCallbacks.Stub,
        showComments = true,
        markedIds = mutableStateListOf(),
        onColumnResized = { _, _ -> },
    )
}