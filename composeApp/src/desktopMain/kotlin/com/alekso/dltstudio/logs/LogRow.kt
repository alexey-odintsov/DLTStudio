package com.alekso.dltstudio.logs

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.alekso.dltstudio.logs.colorfilters.ColorFilterFatal


private val selectedCellStyle = CellStyle(backgroundColor = Color.LightGray)

@Composable
@Preview
fun LogRow(
    modifier: Modifier,
    isSelected: Boolean,
    index: String,
    datetime: String,
    timeOffset: String,
    ecu: String,
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
) {
    Column(
        modifier = modifier.then(
            Modifier.fillMaxWidth()
        )
    ) {
        val finalCellStyle = if (isSelected) selectedCellStyle else cellStyle

        Row(
            modifier
                .height(IntrinsicSize.Max)
                .background(
                    if (isHeader) {
                        Color.Transparent
                    } else if (finalCellStyle != null) {
                        finalCellStyle.backgroundColor ?: Color(250, 250, 250)
                    } else {
                        Color.White
                    }
                )
        ) {
            Cell(
                modifier = Modifier.width(8.dp).padding(end = 2.dp, start = 2.dp),
                textAlign = TextAlign.Center,
                text = if (marked) "⊙" else "",
                isHeader = isHeader,
                cellStyle = finalCellStyle,
            )
            Cell(
                modifier = Modifier.width(54.dp).padding(end = 2.dp),
                textAlign = TextAlign.Right,
                text = index,
                isHeader = isHeader,
                cellStyle = finalCellStyle,
                wrapContent = wrapContent,
            )
            CellDivider()
            Cell(
                modifier = Modifier.width(180.dp),
                textAlign = TextAlign.Center,
                text = datetime,
                isHeader = isHeader,
                cellStyle = finalCellStyle,
                wrapContent = wrapContent,
                )
            CellDivider()
            Cell(
                modifier = Modifier.width(80.dp).padding(end = 2.dp),
                textAlign = TextAlign.Right,
                text = timeOffset,
                isHeader = isHeader,
                cellStyle = finalCellStyle,
                wrapContent = wrapContent,
            )
            CellDivider()
            Cell(
                modifier = Modifier.width(46.dp),
                textAlign = TextAlign.Center,
                text = ecu,
                isHeader = isHeader,
                cellStyle = finalCellStyle,
                wrapContent = wrapContent,
            )
            CellDivider()
            Cell(
                modifier = Modifier.width(46.dp),
                textAlign = TextAlign.Center,
                text = ecuId,
                isHeader = isHeader,
                cellStyle = finalCellStyle,
                wrapContent = wrapContent,
            )
            CellDivider()
            Cell(
                modifier = Modifier.width(46.dp),
                textAlign = TextAlign.Center,
                text = sessionId,
                isHeader = isHeader,
                cellStyle = finalCellStyle,
                wrapContent = wrapContent,
            )
            CellDivider()
            Cell(
                modifier = Modifier.width(46.dp),
                textAlign = TextAlign.Center,
                text = applicationId,
                isHeader = isHeader,
                cellStyle = finalCellStyle,
                wrapContent = wrapContent,
            )
            CellDivider()
            Cell(
                modifier = Modifier.width(46.dp),
                textAlign = TextAlign.Center,
                text = contextId,
                isHeader = isHeader,
                cellStyle = finalCellStyle,
                wrapContent = wrapContent,
            )
            CellDivider()
            Cell(
                modifier = Modifier.width(14.dp)
                    .background(
                        logTypeIndicator?.logTypeStyle?.backgroundColor
                            ?: finalCellStyle?.backgroundColor ?: Color.Transparent
                    ),
                text = logTypeIndicator?.logTypeSymbol ?: "",
                textAlign = TextAlign.Center,
                isHeader = isHeader,
                cellStyle = logTypeIndicator?.logTypeStyle ?: finalCellStyle,
                wrapContent = wrapContent,
            )
            CellDivider()
            Cell(
                modifier = Modifier.weight(1f).padding(start = 6.dp),
                text = content,
                isHeader = isHeader,
                cellStyle = finalCellStyle,
                wrapContent = wrapContent,
            )
        }
        RowDivider()
    }
}

@Composable
fun CellDivider() {
    Box(
        Modifier
            .fillMaxHeight()
            .width(1.dp)
            .background(color = Color.LightGray)
    )
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
        "Content",
        "Another string with _ character",
        "ÄÜË",
        "0123456789-+=<>/?",
        "汉语",
        "~`!@#$%^&*()_+",
        "Fatal error",
        "",
        "",
        "This is the logs content strings, that doesn't fit in one line, let's repeat, This is the logs content strings, that doesn't fit in one line, let's repeat",
    )
    Column(modifier = Modifier.background(Color.Gray)) {
        (0..9).forEach { i ->
            LogRow(
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                isSelected = i == 3,
                index = (16_345_345 + i).toString(),
                datetime = "2024-02-04 18:26:23.074689",
                timeOffset = "1234",
                ecu = if (i % 3 == 0) "汉语" else "EcuI",
                ecuId = "EcuId",
                sessionId = "123",
                applicationId = "AppId",
                contextId = "Con",
                content = contents[i],
                isHeader = i == 0,
                cellStyle = when (i) {
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
            )
        }
    }
}