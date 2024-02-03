package com.alekso.dltstudio.ui.logs

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
    cellStyle: CellStyle? = null
) {
    Column(
        modifier = modifier.then(
            Modifier.fillMaxWidth()
        )
    ) {
        val updatedCellStyle = if (isSelected) selectedCellStyle else cellStyle
        Row {
            Cell(
                modifier = Modifier.width(60.dp),
                textAlign = TextAlign.Right,
                text = index,
                isHeader = isHeader,
                cellStyle = updatedCellStyle
            )
            Cell(
                modifier = Modifier.width(200.dp),
                textAlign = TextAlign.Center,
                text = datetime,
                isHeader = isHeader,
                cellStyle = updatedCellStyle
            )
            Cell(
                modifier = Modifier.width(80.dp),
                textAlign = TextAlign.Right,
                text = timeOffset,
                isHeader = isHeader,
                cellStyle = updatedCellStyle
            )
            Cell(
                modifier = Modifier.width(50.dp),
                textAlign = TextAlign.Center,
                text = ecu,
                isHeader = isHeader,
                cellStyle = updatedCellStyle
            )
            Cell(
                modifier = Modifier.width(50.dp),
                textAlign = TextAlign.Center,
                text = ecuId,
                isHeader = isHeader,
                cellStyle = updatedCellStyle
            )
            Cell(
                modifier = Modifier.width(50.dp),
                textAlign = TextAlign.Center,
                text = sessionId,
                isHeader = isHeader,
                cellStyle = updatedCellStyle
            )
            Cell(
                modifier = Modifier.width(50.dp),
                textAlign = TextAlign.Center,
                text = applicationId,
                isHeader = isHeader,
                cellStyle = updatedCellStyle
            )
            Cell(
                modifier = Modifier.width(50.dp),
                textAlign = TextAlign.Center,
                text = contextId,
                isHeader = isHeader,
                cellStyle = updatedCellStyle
            )
            Cell(
                modifier = Modifier.weight(1f),
                text = content,
                isHeader = isHeader,
                cellStyle = updatedCellStyle
            )
        }
        Divider()
    }

}