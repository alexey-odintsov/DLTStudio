package alexey.odintsov.dltstudio.logs

import alexey.odintsov.dltmessage.SampleData
import alexey.odintsov.dltstudio.logs.colorfilters.ColorFilter
import alexey.odintsov.dltstudio.model.ColumnParams
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import com.alekso.dltstudio.model.contract.LogMessage
import alexey.odintsov.dltstudio.ui.Panel

@Composable
fun LogsListPanel(
    modifier: Modifier = Modifier,
    columnParams: List<ColumnParams>,
    messages: List<LogMessage>,
    colorFilters: SnapshotStateList<ColorFilter>,
    selectedRow: Int,
    logsListState: LazyListState,
    onLogsRowSelected: (Int, Int) -> Unit,
    wrapContent: Boolean,
    rowContextMenuCallbacks: RowContextMenuCallbacks,
    columnsContextMenuCallbacks: ColumnsContextMenuCallbacks,
    showComments: Boolean,
    onColumnResized: (String, Float) -> Unit,
    markedIds: List<Int>,
    comments: Map<Int, String>,
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
            comments = comments,
        )
    }
}

@Preview
@Composable
fun PreviewLogsListPanel() {
    val list = SnapshotStateList<LogMessage>()
    list.addAll(
        SampleData.getSampleDltMessages(20)
            .map { LogMessage(dltMessage = it) })

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
        comments = mutableStateMapOf(),
        onColumnResized = { _, _ -> },
    )
}