package com.alekso.dltstudio.logs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltstudio.RowContextMenuCallbacks
import com.alekso.dltstudio.logs.colorfilters.ColorFilter
import com.alekso.dltstudio.ui.Panel

@Composable
fun LogsListPanel(
    modifier: Modifier = Modifier,
    messages: List<DLTMessage>,
    colorFilters: List<ColorFilter>,
    selectedRow: Int,
    logsListState: LazyListState,
    onLogsRowSelected: (Int, Int) -> Unit,
    wrapContent: Boolean,
    rowContextMenuCallbacks: RowContextMenuCallbacks,
) {
    Panel(
        modifier = modifier,
        title = "Messages"
    ) {
        LazyScrollable(
            Modifier.fillMaxSize().background(Color.LightGray),
            messages,
            null,
            colorFilters,
            selectedRow = selectedRow,
            onRowSelected = onLogsRowSelected,
            listState = logsListState,
            wrapContent = wrapContent,
            rowContextMenuCallbacks = rowContextMenuCallbacks,
        )
    }
}