package com.alekso.dltstudio.logs

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.gestures.detectVerticalDragGestures
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.alekso.dltstudio.RowContextMenuCallbacks
import com.alekso.dltstudio.TimeFormatter
import com.alekso.dltstudio.logs.colorfilters.ColorFilter
import com.alekso.dltstudio.model.LogMessage
import kotlin.math.roundToInt


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyScrollable(
    modifier: Modifier,
    logMessages: SnapshotStateList<LogMessage>,
    indexes: List<Int>? = null,
    colorFilters: List<ColorFilter>,
    selectedRow: Int,
    onRowSelected: (Int, Int) -> Unit,
    listState: LazyListState,
    wrapContent: Boolean,
    rowContextMenuCallbacks: RowContextMenuCallbacks,
    showComments: Boolean,
) {
    val selectedIds = remember { mutableStateOf(emptySet<String>()) }

    Column(modifier = modifier) {

        val horizontalState = rememberScrollState()
        val columnModifier = if (wrapContent) {
            Modifier
        } else {
            Modifier.horizontalScroll(horizontalState).width(3000.dp)
        }

        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(columnModifier.listDragHandler(listState, selectedIds), listState) {
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
                itemsIndexed(
                    items = logMessages,
                    key = { _, log -> log.getKey() },
                    contentType = { _, _ -> LogMessage::class }) { i, logMessage ->

                    val dltMessage = logMessage.dltMessage
                    val cellStyle =
                        colorFilters.firstOrNull { filter -> filter.assess(dltMessage) }?.cellStyle

                    val index: Int = if (indexes != null) indexes[i] else i
                    val sTime: String = TimeFormatter.formatDateTime(dltMessage.timeStampNano)
                    val sTimeOffset: String =
                        if (dltMessage.standardHeader.timeStamp != null) "%.4f".format(dltMessage.standardHeader.timeStamp!!.toLong() / 10000f) else "-"
                    val sEcu: String = dltMessage.ecuId
                    val sEcuId: String = "${dltMessage.standardHeader.ecuId}"
                    val sSessionId: String = "${dltMessage.standardHeader.sessionId}"
                    val sApplicationId: String = "${dltMessage.extendedHeader?.applicationId}"
                    val sContextId: String = "${dltMessage.extendedHeader?.contextId}"
                    val sContent: String = dltMessage.payload
                    val logTypeIndicator: LogTypeIndicator? =
                        LogTypeIndicator.fromMessageType(dltMessage.extendedHeader?.messageInfo?.messageTypeInfo)

                    RowContextMenu(
                        i = i,
                        message = logMessage,
                        rowContent = "$index $sTime $sTimeOffset $sEcu $sEcuId $sSessionId $sApplicationId $sContextId $sContent",
                        rowContextMenuCallbacks = rowContextMenuCallbacks,
                    ) {
                        LogRow(
                            modifier = Modifier.selectable(
                                selected = false, //selectedIds.value.contains(logMessage.getKey()), //i == selectedRow,
                                onClick = {
                                    selectedIds.value = mutableSetOf()
                                    onRowSelected(i, index)
                                }),
                            isSelected = (selectedIds.value.contains(logMessage.getKey())), //(i == selectedRow),
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
                            marked = logMessage.marked,
                            comment = logMessage.comment,
                            showComments = showComments,
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

fun Modifier.listDragHandler(
    listState: LazyListState,
    selectedIds: MutableState<Set<String>>
) = pointerInput(Unit) {
    var isSelectionMode = false
    detectVerticalDragGestures(
        onDragStart = { offset ->
            isSelectionMode = true
            selectedIds.value = mutableSetOf()
            val selectedItemKey = listState.gridItemKeyAtPosition(offset)
            println("selectedItemKey: $selectedItemKey")
        },
        onDragCancel = {
            isSelectionMode = false
        },
        onDragEnd = {
            isSelectionMode = false
        },
        onVerticalDrag = { change, _ ->
            if (isSelectionMode) {
                listState.gridItemKeyAtPosition(change.position)?.let { key ->
                    selectedIds.value = selectedIds.value.plus(key)
                }
            }
        },
    )
}

fun LazyListState.gridItemKeyAtPosition(hitPoint: Offset): String? {
    val selectedItem = layoutInfo.visibleItemsInfo.find { itemInfo ->
        (hitPoint.y.roundToInt() >= itemInfo.offset) && (hitPoint.y.roundToInt() < (itemInfo.offset + itemInfo.size))
    }
    return selectedItem?.key as? String
}