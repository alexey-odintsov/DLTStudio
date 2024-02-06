package com.alekso.dltstudio.logs

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
import com.alekso.dltparser.dlt.DLTMessage
import java.text.SimpleDateFormat
import java.util.Locale

private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH)

@Composable
fun LazyScrollable(
    modifier: Modifier,
    dltMessages: List<DLTMessage>,
    indexes: List<Int>? = null,
    colorFilters: List<CellColorFilter>,
    selectedRow: Int,
    selectedRowCallback: (Int, Int) -> Unit,
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
                items(dltMessages.size) { i ->
                    val message = dltMessages[i]
                    val cellStyle =
                        colorFilters.firstOrNull { it.condition(message) }?.cellStyle

                    val index: Int = if (indexes != null) indexes[i] else i
                    val sTime: String = simpleDateFormat.format(message.getTimeStamp())
                    val sTimeOffset: String =
                        if (message.standardHeader.timeStamp != null) "%.4f".format(message.standardHeader.timeStamp!!.toLong() / 10000f) else "-"
                    val sEcu: String = message.ecuId
                    val sEcuId: String = "${message.standardHeader.ecuId}"
                    val sSessionId: String = "${message.standardHeader.sessionId}"
                    val sApplicationId: String = "${message.extendedHeader?.applicationId}"
                    val sContextId: String = "${message.extendedHeader?.contextId}"
                    val sContent: String = "${message.payload?.asText()}"

                    RowContextMenu(
                        i,
                        message,
                        "$index $sTime $sTimeOffset $sEcu $sEcuId $sSessionId $sApplicationId $sContextId $sContent"
                    ) {
                        LogRow(
                            modifier = Modifier.selectable(
                                selected = i == selectedRow,
                                onClick = { selectedRowCallback(i, index) }),
                            isSelected = (i == selectedRow),
                            index.toString(),
                            sTime,
                            sTimeOffset,
                            sEcu,
                            sEcuId,
                            sSessionId,
                            sApplicationId,
                            sContextId,
                            sContent,
                            cellStyle = cellStyle
                        )
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