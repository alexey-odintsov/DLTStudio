package com.alekso.dltstudio.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
@Preview
fun LogRow(
    index: String,
    datetime: String,
    timeOffset: String,
    ecu: String,
    ecuId: String,
    sessionId: String,
    applicationId: String,
    contextId: String,
    content: String,
    isHeader: Boolean = false
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(enabled = true, selected = true, onClick = {

            })
    ) {
        Row {
            Cell(
                modifier = Modifier.width(60.dp),
                textAlign = TextAlign.Right,
                text = index,
                isHeader = isHeader
            )
            Cell(
                modifier = Modifier.width(200.dp),
                textAlign = TextAlign.Center,
                text = datetime,
                isHeader = isHeader
            )
            Cell(
                modifier = Modifier.width(80.dp),
                textAlign = TextAlign.Right,
                text = timeOffset,
                isHeader = isHeader
            )
            Cell(
                modifier = Modifier.width(50.dp),
                textAlign = TextAlign.Center,
                text = ecu,
                isHeader = isHeader
            )
            Cell(
                modifier = Modifier.width(50.dp),
                textAlign = TextAlign.Center,
                text = ecuId,
                isHeader = isHeader
            )
            Cell(
                modifier = Modifier.width(50.dp),
                textAlign = TextAlign.Center,
                text = sessionId,
                isHeader = isHeader
            )
            Cell(
                modifier = Modifier.width(50.dp),
                textAlign = TextAlign.Center,
                text = applicationId,
                isHeader = isHeader
            )
            Cell(
                modifier = Modifier.width(50.dp),
                textAlign = TextAlign.Center,
                text = contextId,
                isHeader = isHeader
            )
            Cell(
                modifier = Modifier.width(800.dp),
                text = content,
                isHeader = isHeader
            )
        }
    }

}