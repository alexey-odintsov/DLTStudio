package com.alekso.dltstudio.logs

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


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
    logTypeIndicator: LogTypeIndicator? = null
) {
    Column(
        modifier = modifier.then(
            Modifier.fillMaxWidth()
        )
    ) {
        val updatedCellStyle = if (isSelected) selectedCellStyle else cellStyle
        Row {
            Cell(
                modifier = Modifier.width(54.dp).padding(end = 2.dp),
                textAlign = TextAlign.Right,
                text = index,
                isHeader = isHeader,
                cellStyle = updatedCellStyle
            )
            Cell(
                modifier = Modifier.width(180.dp),
                textAlign = TextAlign.Center,
                text = datetime,
                isHeader = isHeader,
                cellStyle = updatedCellStyle
            )
            Cell(
                modifier = Modifier.width(80.dp).padding(end = 2.dp),
                textAlign = TextAlign.Right,
                text = timeOffset,
                isHeader = isHeader,
                cellStyle = updatedCellStyle
            )
            Cell(
                modifier = Modifier.width(46.dp),
                textAlign = TextAlign.Center,
                text = ecu,
                isHeader = isHeader,
                cellStyle = updatedCellStyle
            )
            Cell(
                modifier = Modifier.width(46.dp),
                textAlign = TextAlign.Center,
                text = ecuId,
                isHeader = isHeader,
                cellStyle = updatedCellStyle
            )
            Cell(
                modifier = Modifier.width(46.dp),
                textAlign = TextAlign.Center,
                text = sessionId,
                isHeader = isHeader,
                cellStyle = updatedCellStyle
            )
            Cell(
                modifier = Modifier.width(46.dp),
                textAlign = TextAlign.Center,
                text = applicationId,
                isHeader = isHeader,
                cellStyle = updatedCellStyle
            )
            Cell(
                modifier = Modifier.width(46.dp),
                textAlign = TextAlign.Center,
                text = contextId,
                isHeader = isHeader,
                cellStyle = updatedCellStyle
            )
            Cell(
                modifier = Modifier.width(14.dp),
                text = logTypeIndicator?.logTypeSymbol ?: "",
                textAlign = TextAlign.Center,
                isHeader = isHeader,
                cellStyle = logTypeIndicator?.logTypeStyle ?: updatedCellStyle
            )
            Cell(
                modifier = Modifier.weight(1f).padding(start = 6.dp),
                text = content,
                isHeader = isHeader,
                cellStyle = updatedCellStyle
            )
        }
        Divider()
    }

}

@Preview
@Composable
fun LogRowPreview() {
    Column(modifier = Modifier.background(Color.Gray)) {
        (1..10).forEach { i ->
            LogRow(
                modifier = Modifier.fillMaxWidth(),
                isSelected = false,
                index = (16_345_345 + i).toString(),
                datetime = "2024-02-04 18:26:23.074689",
                timeOffset = "1234",
                ecu = if (i % 3 == 0) "汉语" else "EcuI",
                sessionId = "123",
                applicationId = "AppId",
                ecuId = "EcuId",
                contextId = "Con",
                content = "Content goes here",
            )
        }
    }
}