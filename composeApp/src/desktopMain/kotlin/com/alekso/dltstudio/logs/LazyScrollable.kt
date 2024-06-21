package com.alekso.dltstudio.logs

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.selection.selectable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltstudio.RowContextMenuCallbacks
import com.alekso.dltstudio.TimeFormatter
import com.alekso.dltstudio.logs.colorfilters.ColorFilter


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyScrollable(
    modifier: Modifier,
    dltMessages: List<DLTMessage>,
    indexes: List<Int>? = null,
    colorFilters: List<ColorFilter>,
    selectedRow: Int,
    onRowSelected: (Int, Int) -> Unit,
    listState: LazyListState,
    wrapContent: Boolean,
    rowContextMenuCallbacks: RowContextMenuCallbacks,
) {
    Column(modifier = modifier) {

        val horizontalState = rememberScrollState()
        val columnModifier = if (wrapContent) {
            Modifier
        } else {
            Modifier.horizontalScroll(horizontalState).width(3000.dp)
        }

        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(columnModifier, listState) {
                stickyHeader {
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
                        wrapContent = wrapContent,
                    )
                }

                items(dltMessages.size) { i ->
                    val message = dltMessages[i]
                    val cellStyle =
                        colorFilters.firstOrNull { filter -> filter.assess(message) }?.cellStyle

                    val index: Int = if (indexes != null) indexes[i] else i
                    val sTime: String = TimeFormatter.formatDateTime(message.timeStampNano)
                    val sTimeOffset: String =
                        if (message.standardHeader.timeStamp != null) "%.4f".format(message.standardHeader.timeStamp!!.toLong() / 10000f) else "-"
                    val sEcu: String = message.ecuId
                    val sEcuId: String = "${message.standardHeader.ecuId}"
                    val sSessionId: String = "${message.standardHeader.sessionId}"
                    val sApplicationId: String = "${message.extendedHeader?.applicationId}"
                    val sContextId: String = "${message.extendedHeader?.contextId}"
                    val sContent: String = "${message.payload}"
                    val logTypeIndicator: LogTypeIndicator? =
                        LogTypeIndicator.fromMessageType(message.extendedHeader?.messageInfo?.messageTypeInfo)

                    RowContextMenu(
                        i = i,
                        message = message,
                        rowContextMenuCallbacks = rowContextMenuCallbacks,
                        rowContent = "$index $sTime $sTimeOffset $sEcu $sEcuId $sSessionId $sApplicationId $sContextId $sContent"
                    ) {
                        LogRow(
                            modifier = Modifier.selectable(
                                selected = i == selectedRow,
                                onClick = { onRowSelected(i, index) }),
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
                            cellStyle = cellStyle,
                            logTypeIndicator = logTypeIndicator,
                            wrapContent = wrapContent,
                        )
                    }
                }

            }
            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                adapter = rememberScrollbarAdapter(
                    scrollState = listState
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