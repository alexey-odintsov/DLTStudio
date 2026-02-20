package alexey.odintsov.dltstudio.logs

import alexey.odintsov.dltstudio.logs.colorfilters.ColorFilter
import alexey.odintsov.dltstudio.model.ColumnParams
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import com.alekso.dltstudio.model.contract.LogMessage
import alexey.odintsov.dltstudio.ui.Panel

@Composable
fun SearchResultsPanel(
    modifier: Modifier = Modifier,
    columnParams: List<ColumnParams>,
    searchResult: List<LogMessage>,
    colorFilters: SnapshotStateList<ColorFilter>,
    searchResultSelectedRow: Int,
    searchListState: LazyListState,
    onSearchRowSelected: (Int, Int) -> Unit,
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