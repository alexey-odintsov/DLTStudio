package com.alekso.dltstudio.ui

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
fun LogRow(index: Int, message: DLTMessage, dateFormat: DateFormat) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row {
            Cell(
                modifier = Modifier.width(30.dp),
                textAlign = TextAlign.Right,
                text = index.toString()
            )
            Cell(
                modifier = Modifier.width(220.dp),
                text = dateFormat.format(message.timeStampSec * 1000L + message.timeStampUs / 1000)
            )
            Cell(
                modifier = Modifier.width(50.dp),
                text = message.ecuId
            )
            Cell(
                modifier = Modifier.width(50.dp),
                text = "${message.standardHeader.ecuId}"
            )
            Cell(
                modifier = Modifier.width(50.dp),
                text = "${message.standardHeader.sessionId}"
            )
            Cell(
                modifier = Modifier.width(50.dp),
                text = "${message.extendedHeader?.applicationId}"
            )
            Cell(
                modifier = Modifier.width(50.dp),
                text = "${message.extendedHeader?.contextId}"
            )
            Cell(
                modifier = Modifier.width(800.dp),
                text = "${message.payload?.asText()}"
            )
        }
    }

}