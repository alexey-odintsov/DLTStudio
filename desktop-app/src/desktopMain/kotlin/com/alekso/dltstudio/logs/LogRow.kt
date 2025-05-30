package com.alekso.dltstudio.logs

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.alekso.dltstudio.logs.colorfilters.ColorFilterFatal
import com.alekso.dltstudio.model.ColumnParams
import dltstudio.resources.Res
import dltstudio.resources.icon_mark
import org.jetbrains.compose.resources.painterResource
import java.awt.Cursor


private val selectedCellStyle = CellStyle(backgroundColor = Color.LightGray)

@Composable
@Preview
fun LogRow(
    modifier: Modifier,
    columnParams: SnapshotStateList<ColumnParams>,
    isSelected: Boolean,
    index: String,
    datetime: String,
    timeOffset: String,
    messageCounter: String,
    ecuId: String,
    sessionId: String,
    applicationId: String,
    contextId: String,
    content: String,
    isHeader: Boolean = false,
    cellStyle: CellStyle? = null,
    logTypeIndicator: LogTypeIndicator? = null,
    wrapContent: Boolean,
    marked: Boolean = false,
    comment: String? = null,
    showComments: Boolean = false,
    onColumnResized: (String, Float) -> Unit,
) {
    Column(
        modifier = modifier.then(
            Modifier.fillMaxWidth()
        )
    ) {
        val finalCellStyle = if (isSelected) selectedCellStyle else cellStyle

        Row(
            modifier.height(IntrinsicSize.Max).background(
                if (isHeader) {
                    Color.White
                } else if (finalCellStyle != null) {
                    finalCellStyle.backgroundColor ?: Color(250, 250, 250)
                } else {
                    Color.White
                }
            )
        ) {
            if (columnParams[0].visible) {
                Cell(
                    modifier = Modifier.width(columnParams[0].size.dp)
                        .padding(end = 2.dp, start = 2.dp, top = 2.dp),
                    textAlign = TextAlign.Center,
                    text = "",
                    isHeader = isHeader,
                    cellStyle = finalCellStyle,
                ) {
                    if (marked) {
                        Image(
                            painterResource(Res.drawable.icon_mark),
                            contentDescription = "Mark log",
                            modifier = Modifier.size(6.dp),
                        )
                    } else {
                        Box(modifier = Modifier.size(6.dp))
                    }
                }
                CellDivider(
                    resizeable = isHeader,
                    key = columnParams[0].column.name,
                    onResized = onColumnResized,
                )
            }
            if (columnParams[1].visible) {
                Cell(
                    modifier = Modifier.width(columnParams[1].size.dp).padding(end = 2.dp),
                    textAlign = TextAlign.Right,
                    text = index,
                    isHeader = isHeader,
                    cellStyle = finalCellStyle,
                    wrapContent = wrapContent,
                )
                CellDivider(
                    resizeable = isHeader,
                    key = columnParams[1].column.name,
                    onResized = onColumnResized,
                )
            }
            if (columnParams[2].visible) {
                Cell(
                    modifier = Modifier.width(columnParams[2].size.dp),
                    textAlign = TextAlign.Center,
                    text = datetime,
                    isHeader = isHeader,
                    cellStyle = finalCellStyle,
                    wrapContent = wrapContent,
                )
                CellDivider(
                    resizeable = isHeader,
                    key = columnParams[2].column.name,
                    onResized = onColumnResized,
                )
            }
            if (columnParams[3].visible) {
                Cell(
                    modifier = Modifier.width(columnParams[3].size.dp).padding(end = 2.dp),
                    textAlign = TextAlign.Right,
                    text = timeOffset,
                    isHeader = isHeader,
                    cellStyle = finalCellStyle,
                    wrapContent = wrapContent,
                )
                CellDivider(
                    resizeable = isHeader,
                    key = columnParams[3].column.name,
                    onResized = onColumnResized,
                )
            }
            if (columnParams[4].visible) {
                Cell(
                    modifier = Modifier.width(columnParams[4].size.dp).padding(end = 2.dp),
                    textAlign = TextAlign.Right,
                    text = messageCounter,
                    isHeader = isHeader,
                    cellStyle = finalCellStyle,
                    wrapContent = wrapContent,
                )
                CellDivider(
                    resizeable = isHeader,
                    key = columnParams[4].column.name,
                    onResized = onColumnResized,
                )
            }
            if (columnParams[5].visible) {
                Cell(
                    modifier = Modifier.width(columnParams[5].size.dp),
                    textAlign = TextAlign.Center,
                    text = ecuId,
                    isHeader = isHeader,
                    cellStyle = finalCellStyle,
                    wrapContent = wrapContent,
                )
                CellDivider(
                    resizeable = isHeader,
                    key = columnParams[5].column.name,
                    onResized = onColumnResized,
                )
            }
            if (columnParams[6].visible) {
                Cell(
                    modifier = Modifier.width(columnParams[6].size.dp),
                    textAlign = TextAlign.Center,
                    text = sessionId,
                    isHeader = isHeader,
                    cellStyle = finalCellStyle,
                    wrapContent = wrapContent,
                )
                CellDivider(
                    resizeable = isHeader,
                    key = columnParams[6].column.name,
                    onResized = onColumnResized,
                )
            }
            if (columnParams[7].visible) {
                Cell(
                    modifier = Modifier.width(columnParams[7].size.dp),
                    textAlign = TextAlign.Center,
                    text = applicationId,
                    isHeader = isHeader,
                    cellStyle = finalCellStyle,
                    wrapContent = wrapContent,
                )
                CellDivider(
                    resizeable = isHeader,
                    key = columnParams[7].column.name,
                    onResized = onColumnResized,
                )
            }
            if (columnParams[8].visible) {
                Cell(
                    modifier = Modifier.width(columnParams[8].size.dp),
                    textAlign = TextAlign.Center,
                    text = contextId,
                    isHeader = isHeader,
                    cellStyle = finalCellStyle,
                    wrapContent = wrapContent,
                )
                CellDivider(
                    resizeable = isHeader,
                    key = columnParams[8].column.name,
                    onResized = onColumnResized,
                )
            }
            if (columnParams[9].visible) {
                Cell(
                    modifier = Modifier.width(columnParams[9].size.dp).background(
                        logTypeIndicator?.logTypeStyle?.backgroundColor
                            ?: finalCellStyle?.backgroundColor ?: Color.Transparent
                    ),
                    text = logTypeIndicator?.logTypeSymbol ?: "",
                    textAlign = TextAlign.Center,
                    isHeader = isHeader,
                    cellStyle = logTypeIndicator?.logTypeStyle ?: finalCellStyle,
                    wrapContent = wrapContent,
                )
                CellDivider(
                    resizeable = isHeader,
                    key = columnParams[9].column.name,
                    onResized = onColumnResized,
                )
            }
            Cell(
                modifier = Modifier.weight(1f).padding(start = 6.dp),
                text = content,
                isHeader = isHeader,
                cellStyle = finalCellStyle,
                wrapContent = wrapContent,
            )
        }
        if (showComments && comment != null) {
            RowDivider()
            Row(
                modifier.height(IntrinsicSize.Max).background(
                    if (finalCellStyle != null) {
                        finalCellStyle.backgroundColor ?: Color(250, 250, 250)
                    } else {
                        Color.White
                    }
                )
            ) {
                Cell(
                    modifier = Modifier.weight(1f).padding(start = 6.dp),
                    cellStyle = finalCellStyle,
                    text = comment,
                    isComment = true,
                    wrapContent = wrapContent,
                )
            }
        }
        RowDivider()
    }
}

@Composable
fun CellDivider(
    modifier: Modifier = Modifier,
    resizeable: Boolean = false,
    key: String = "",
    onResized: ((String, Float) -> Unit) = { _, _ -> }
) {
    var finalModifier = modifier.fillMaxHeight().width(1.dp).background(color = Color.LightGray)

    if (resizeable) {
        finalModifier = finalModifier.pointerHoverIcon(PointerIcon(Cursor(Cursor.E_RESIZE_CURSOR)))
            .pointerInput("divider-$key") {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    onResized(key, dragAmount.x / 2f) // TODO: why is it 2x bigger?
                }
            }
    }
    Box(finalModifier)
}

@Composable
fun RowDivider() {
    Box(
        Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(color = Color.LightGray)
    )
}

@Preview
@Composable
fun LogRowPreview() {
    val contents = listOf(
        listOf("Content", null),
        listOf("Another string with _ character", "This is a comment"),
        listOf("ÄÜË", null),
        listOf("0123456789-+=<>/?", "These strange numbers, they should mean something.."),
        listOf("汉语", null),
        listOf("~`!@#$%^&*()_+", null),
        listOf("Fatal error", "Wow, fatal error!"),
        listOf("", null),
        listOf("hey-ho!", "No way, I've found that comment at last!"),
        listOf(
            "This is the logs content strings, that doesn't fit in one line, let's repeat, This is the logs content strings, that doesn't fit in one line, let's repeat",
            "A warning message - to research what does it mean. A warning message - to research what does it mean. A warning message - to research what does it mean. A warning message - to research what does it mean. A warning message - to research what does it mean. "
        ),
    )
    Column(modifier = Modifier.background(Color.Gray)) {
        (0..9).forEach { i ->
            LogRow(
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                columnParams = mutableStateListOf(*ColumnParams.DefaultParams.toTypedArray()),
                isSelected = i == 3,
                index = (16_345_345 + i).toString(),
                datetime = "2024-02-04 18:26:23.074689",
                timeOffset = "1234",
                messageCounter = "1",
                ecuId = "EcuId",
                sessionId = "123",
                applicationId = "AppId",
                contextId = "Con",
                content = contents[i][0] ?: "",
                isHeader = i == 0,
                cellStyle = when (i) {
                    6 -> CellStyle(backgroundColor = Color.Green)
                    9 -> CellStyle(backgroundColor = Color.Yellow)
                    8 -> CellStyle(
                        backgroundColor = Color(0xE7, 0x62, 0x29),
                        textColor = Color.White
                    )

                    else -> null
                },
                logTypeIndicator = if (i == 6) LogTypeIndicator(
                    "F",
                    ColorFilterFatal.cellStyle
                ) else null,
                wrapContent = true,
                marked = i % 2 == 0,
                comment = contents[i][1],
                showComments = true,
                onColumnResized = { _, _ -> })
        }
    }
}