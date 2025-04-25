package com.alekso.dltstudio.logs

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.alekso.dltstudio.logs.colorfilters.ColorFilter
import com.alekso.dltstudio.model.ColumnParams
import com.alekso.dltstudio.model.contract.LogMessage
import com.alekso.dltstudio.ui.Panel
import com.alekso.dltmessage.SampleData

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
) {
    Panel(
        modifier = modifier,
        title = "Messages"
    ) {
        LazyScrollable(
            Modifier.fillMaxSize().background(Color.LightGray),
            columnParams,
            messages,
            null,
            colorFilters,
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
            .map { LogMessage(dltMessage = it, marked = true, comment = "Test comment") })

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
        onColumnResized = { _, _ -> }
    )
}