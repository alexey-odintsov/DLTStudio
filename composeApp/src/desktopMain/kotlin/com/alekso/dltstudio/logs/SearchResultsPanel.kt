package com.alekso.dltstudio.logs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltstudio.logs.colorfilters.ColorFilter
import com.alekso.dltstudio.ui.Panel

@Composable
fun SearchResultsPanel(
    modifier: Modifier = Modifier,
    searchResult: List<DLTMessage>,
    searchIndexes: List<Int>,
    colorFilters: List<ColorFilter>,
    searchResultSelectedRow: Int,
    searchListState: LazyListState,
    onSearchRowSelected: (Int, Int) -> Unit,
) {
    Panel(
        modifier = modifier,
        title = "Search results"
    ) {
        LazyScrollable(
            Modifier.fillMaxSize().background(Color.LightGray),
            searchResult,
            searchIndexes,
            colorFilters,
            selectedRow = searchResultSelectedRow,
            onRowSelected = onSearchRowSelected,
            listState = searchListState,
        )
    }

}