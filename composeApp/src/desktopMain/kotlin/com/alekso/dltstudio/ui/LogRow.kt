package com.alekso.dltstudio.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.alekso.dltparser.dlt.DLTMessage
import java.text.DateFormat

@Composable
@Preview
fun LogRow(
    index: String,
    datetime: String,
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
    ) {
        Row {
            Cell(
                modifier = Modifier.width(30.dp),
                textAlign = TextAlign.Right,
                text = index,
                isHeader = isHeader
            )
            Cell(
                modifier = Modifier.width(200.dp),
                text = datetime,
                isHeader = isHeader
            )
            Cell(
                modifier = Modifier.width(50.dp),
                text = ecu,
                isHeader = isHeader
            )
            Cell(
                modifier = Modifier.width(50.dp),
                text = ecuId,
                isHeader = isHeader
            )
            Cell(
                modifier = Modifier.width(50.dp),
                text = sessionId,
                isHeader = isHeader
            )
            Cell(
                modifier = Modifier.width(50.dp),
                text = applicationId,
                isHeader = isHeader
            )
            Cell(
                modifier = Modifier.width(50.dp),
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