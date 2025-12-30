package com.alekso.dltstudio.logs

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import com.alekso.dltstudio.logs.colorfilters.ColorFilter
import com.alekso.dltstudio.model.ColumnParams
import com.alekso.dltstudio.model.contract.LogMessage
import com.alekso.dltstudio.ui.Panel

@Composable
fun SearchResultsPanel(
    modifier: Modifier = Modifier,
    columnParams: SnapshotStateList<ColumnParams>,
    searchResult: SnapshotStateList<LogMessage>,
    colorFilters: SnapshotStateList<ColorFilter>,
    searchResultSelectedRow: Int,
    searchListState: LazyListState,
    onSearchRowSelected: (Int, Int) -> Unit,
    wrapContent: Boolean,
    rowContextMenuCallbacks: RowContextMenuCallbacks,
    columnsContextMenuCallbacks: ColumnsContextMenuCallbacks,
    showComments: Boolean,
    onColumnResized: (String, Float) -> Unit,
    markedIds: SnapshotStateList<Int>,
    comments: SnapshotStateMap<Int, String>,
) {
    Panel(
        modifier = modifier,
        title = if (searchResult.isNotEmpty()) "Search results: ${searchResult.size} items" else "Search results"
    ) {
        LazyScrollable(
            Modifier.fillMaxSize(),
            columnParams = columnParams,
            logMessages = searchResult,
            colorFilters = colorFilters,
            selectedRow = searchResultSelectedRow,
            onRowSelected = onSearchRowSelected,
            listState = searchListState,
            wrapContent = wrapContent,
            rowContextMenuCallbacks = rowContextMenuCallbacks,
            columnsContextMenuCallbacks = columnsContextMenuCallbacks,
            showComments = showComments,
            onColumnResized = onColumnResized,
            markedIds = markedIds,
            comments = comments,
        )
    }

}