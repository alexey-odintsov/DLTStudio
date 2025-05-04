package com.alekso.dltstudio.logs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    onSearchRowSelected: (Int, String) -> Unit,
    wrapContent: Boolean,
    rowContextMenuCallbacks: RowContextMenuCallbacks,
    columnsContextMenuCallbacks: ColumnsContextMenuCallbacks,
    showComments: Boolean,
    onColumnResized: (String, Float) -> Unit,
) {
    Panel(
        modifier = modifier,
        title = if (searchResult.isNotEmpty()) "Search results: ${searchResult.size} items" else "Search results"
    ) {
        LazyScrollable(
            Modifier.fillMaxSize().background(Color.LightGray),
            columnParams = columnParams,
            searchResult,
            colorFilters,
            selectedRow = searchResultSelectedRow,
            onRowSelected = onSearchRowSelected,
            listState = searchListState,
            wrapContent = wrapContent,
            showComments = showComments,
            rowContextMenuCallbacks = rowContextMenuCallbacks,
            columnsContextMenuCallbacks = columnsContextMenuCallbacks,
            onColumnResized = onColumnResized,
        )
    }

}