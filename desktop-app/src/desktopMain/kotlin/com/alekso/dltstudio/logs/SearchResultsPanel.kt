package com.alekso.dltstudio.logs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.alekso.dltstudio.com.alekso.dltstudio.logs.ColumnsContextMenuCallbacks
import com.alekso.dltstudio.logs.colorfilters.ColorFilter
import com.alekso.dltstudio.model.contract.LogMessage
import com.alekso.dltstudio.ui.Panel

@Composable
fun SearchResultsPanel(
    modifier: Modifier = Modifier,
    columnParams: SnapshotStateList<ColumnsParams>,
    searchResult: SnapshotStateList<LogMessage>,
    searchIndexes: SnapshotStateList<Int>,
    colorFilters: SnapshotStateList<ColorFilter>,
    searchResultSelectedRow: Int,
    searchListState: LazyListState,
    onSearchRowSelected: (Int, Int) -> Unit,
    wrapContent: Boolean,
    rowContextMenuCallbacks: RowContextMenuCallbacks,
    columnsContextMenuCallbacks: ColumnsContextMenuCallbacks,
    showComments: Boolean,

    ) {
    Panel(
        modifier = modifier,
        title = if (searchResult.isNotEmpty()) "Search results: ${searchResult.size} items" else "Search results"
    ) {
        LazyScrollable(
            Modifier.fillMaxSize().background(Color.LightGray),
            columnParams = columnParams,
            searchResult,
            searchIndexes,
            colorFilters,
            selectedRow = searchResultSelectedRow,
            onRowSelected = onSearchRowSelected,
            listState = searchListState,
            wrapContent = wrapContent,
            showComments = showComments,
            rowContextMenuCallbacks = rowContextMenuCallbacks,
            columnsContextMenuCallbacks = columnsContextMenuCallbacks,
        )
    }

}