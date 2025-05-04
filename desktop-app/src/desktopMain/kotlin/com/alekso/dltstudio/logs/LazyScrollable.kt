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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.selection.selectable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.alekso.dltstudio.LocalFormatter
import com.alekso.dltstudio.logs.colorfilters.ColorFilter
import com.alekso.dltstudio.model.ColumnParams
import com.alekso.dltstudio.model.contract.LogMessage


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyScrollable(
    modifier: Modifier,
    columnParams: SnapshotStateList<ColumnParams>,
    logMessages: SnapshotStateList<LogMessage>,
    colorFilters: SnapshotStateList<ColorFilter>,
    selectedRow: Int,
    onRowSelected: (Int, Int) -> Unit,
    listState: LazyListState,
    wrapContent: Boolean,
    rowContextMenuCallbacks: RowContextMenuCallbacks,
    columnsContextMenuCallbacks: ColumnsContextMenuCallbacks,
    showComments: Boolean,
    onColumnResized: (String, Float) -> Unit,
) {
    Column(modifier = modifier) {

        val horizontalState = rememberScrollState()
        val columnModifier = if (wrapContent) {
            Modifier
        } else {
            Modifier.horizontalScroll(horizontalState).width(3000.dp)
        }
        val focusManager = LocalFocusManager.current

        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(
                columnModifier
                    .onKeyEvent(onKeyEvent = { e ->
                        if (e.type == KeyEventType.KeyDown) {
                            return@onKeyEvent when (e.key) {
                                Key.S, Key.DirectionDown -> {
                                    focusManager.moveFocus(FocusDirection.Down)
                                    true
                                }
                                Key.W, Key.DirectionUp -> {
                                    focusManager.moveFocus(FocusDirection.Up)
                                    true
                                }
                                else -> false
                            }
                        }
                        false
                    }),
                listState
            ) {
                stickyHeader {
                    ColumnsContextMenu(
                        columnParams,
                        columnsContextMenuCallbacks
                    ) {
                        LogRow(
                            modifier = Modifier,
                            columnParams = columnParams,
                            isSelected = false,
                            "#",
                            "DateTime",
                            "Time",
                            "Count",
                            "ecuId",
                            "sessId",
                            "appId",
                            "ctxId",
                            "content",
                            wrapContent = wrapContent,
                            onColumnResized = onColumnResized,
                            isHeader = true,
                        )
                    }
                }
                itemsIndexed(
                    items = logMessages,
                    key = { _, log -> log.key },
                    contentType = { _, _ -> LogMessage::class }) { i, logMessage ->

                    val dltMessage = logMessage.dltMessage
                    val cellStyle =
                        colorFilters.firstOrNull { filter -> filter.assess(dltMessage) }?.cellStyle

                    val index: Int = logMessage.num
                    val sTime: String =
                        LocalFormatter.current.formatDateTime(dltMessage.timeStampUs)
                    val sTimeOffset: String =
                        if (dltMessage.standardHeader.timeStamp != null) "%.4f".format(dltMessage.standardHeader.timeStamp!!.toLong() / 10000f) else "-"
                    val sEcuId = "${dltMessage.standardHeader.ecuId}"
                    val sSessionId = "${dltMessage.standardHeader.sessionId}"
                    val sMessageCounterId = "${dltMessage.standardHeader.messageCounter}"
                    val sApplicationId = "${dltMessage.extendedHeader?.applicationId}"
                    val sContextId = "${dltMessage.extendedHeader?.contextId}"
                    val sContent: String = dltMessage.payloadText()
                    val logTypeIndicator: LogTypeIndicator? =
                        LogTypeIndicator.fromMessageType(dltMessage.extendedHeader?.messageInfo?.messageTypeInfo)

                    RowContextMenu(
                        i = i,
                        message = logMessage,
                        rowContent = "$index $sTime $sTimeOffset $sEcuId $sSessionId $sApplicationId $sContextId $sContent",
                        rowContextMenuCallbacks = rowContextMenuCallbacks,
                    ) {
                        LogRow(
                            modifier = Modifier
                                .onFocusChanged { state ->
                                    if (state.isFocused) {
                                        onRowSelected(i, logMessage.num)
                                    }
                                }
                                .selectable(
                                    selected = i == selectedRow,
                                    onClick = { onRowSelected(i, logMessage.num) }
                                )
                                .onKeyEvent { e ->
                                    if (e.type == KeyEventType.KeyDown) {
                                        when (e.key) {
                                            Key.Spacebar -> {
                                                rowContextMenuCallbacks.onMarkClicked(i, logMessage)
                                                true
                                            }

                                            else -> false
                                        }
                                    } else false
                                },
                            columnParams = columnParams,
                            isSelected = (i == selectedRow),
                            index.toString(),
                            sTime,
                            sTimeOffset,
                            sMessageCounterId,
                            sEcuId,
                            sSessionId,
                            sApplicationId,
                            sContextId,
                            sContent,
                            cellStyle = cellStyle,
                            logTypeIndicator = logTypeIndicator,
                            wrapContent = wrapContent,
                            marked = logMessage.marked,
                            comment = logMessage.comment,
                            showComments = showComments,
                            onColumnResized = onColumnResized,
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