package com.alekso.dltstudio.logs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltstudio.ui.Panel

@Composable
fun LogsListPanel(
    modifier: Modifier = Modifier,
    messages: List<DLTMessage>,
    colorFilters: List<CellColorFilter>,
    selectedRow: Int,
    selectedRowCallback: (Int) -> Unit
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
            selectedRowCallback = selectedRowCallback
        )
    }
}