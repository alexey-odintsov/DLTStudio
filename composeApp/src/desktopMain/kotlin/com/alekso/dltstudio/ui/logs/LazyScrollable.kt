package com.alekso.dltstudio.ui.logs

import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.selection.selectable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alekso.dltstudio.ui.ParseSession
import java.text.SimpleDateFormat
import java.util.Locale

private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH)

@Composable
fun LazyScrollable(
    modifier: Modifier,
    dltSession: ParseSession?,
    colorFilters: List<CellColorFilter>,
    selectedRow: Int,
    selectedRowCallback: (Int) -> Unit,
) {
    Column(modifier = modifier) {

        val state = rememberLazyListState()
        val horizontalState = rememberScrollState()

        LogRow(
            modifier = Modifier,
            isSelected = false,
            "#",
            "DateTime",
            "Time",
            "ecu",
            "ecuId",
            "sessId",
            "appId",
            "ctxId",
            "content",
        )

        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(Modifier.horizontalScroll(horizontalState).width(2000.dp), state) {
                if (dltSession != null) {
                    items(dltSession.dltMessages.size) { i ->
                        val message = dltSession.dltMessages[i]
                        RowContextMenu {
                            val cellStyle =
                                colorFilters.firstOrNull { it.condition(message) }?.cellStyle
                            LogRow(
                                modifier = Modifier.selectable(
                                    selected = i == selectedRow,// todo: highlight selected row
                                    onClick = {
                                        selectedRowCallback.invoke(i)
                                    }),
                                isSelected = (i == selectedRow),
                                i.toString(),
                                simpleDateFormat.format(message.getTimeStamp()),
                                if (message.standardHeader.timeStamp != null) "%.4f".format(message.standardHeader.timeStamp!!.toLong() / 10000f) else "-",
                                message.ecuId,
                                "${message.standardHeader.ecuId}",
                                "${message.standardHeader.sessionId}",
                                "${message.extendedHeader?.applicationId}",
                                "${message.extendedHeader?.contextId}",
                                "${message.payload?.asText()}",
                                cellStyle = cellStyle
                            )
                        }
                    }
                }
            }
            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                adapter = rememberScrollbarAdapter(
                    scrollState = state
                )
            )
            HorizontalScrollbar(
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
                adapter = rememberScrollbarAdapter(
                    scrollState = horizontalState
                )
            )
        }
    }
}