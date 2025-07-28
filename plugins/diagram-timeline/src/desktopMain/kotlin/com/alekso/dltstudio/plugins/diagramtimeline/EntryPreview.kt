package com.alekso.dltstudio.plugins.diagramtimeline

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alekso.dltmessage.SampleData
import com.alekso.dltstudio.charts.model.ChartEntry
import com.alekso.dltstudio.charts.model.EventEntry
import com.alekso.dltstudio.model.contract.LogMessage

@Composable
fun EntryPreview(selectedEntry: ChartEntry<LogMessage>?) {
    val padding = Modifier.padding(4.dp)
    val logMessage = selectedEntry?.data
    if (selectedEntry != null) {
        val scrollState = rememberScrollState()
        Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            SelectionContainer {
                Column(Modifier.verticalScroll(scrollState).fillMaxSize()) {
                    Text(text = "Value: ${selectedEntry.getText()}", modifier = padding)
                    val dltMessage = logMessage?.dltMessage
                    if (dltMessage != null) {
                        val timeText = LocalFormatter.current.formatDateTime(dltMessage.timeStampUs)
                        Text(
                            text = "#${logMessage.id} $timeText ${logMessage.getMessageText()}",
                            modifier = padding
                        )
                    }
                }
            }
            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                adapter = rememberScrollbarAdapter(
                    scrollState = scrollState
                )
            )
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("No event selected")
            }
        }
    }
}

@Preview
@Composable
fun PreviewEntryPreview() {
    val dltMessage = SampleData.create(123L, payloadText = "Test message content")
    Column {
        EntryPreview(EventEntry<LogMessage>(23423423L, "test", LogMessage(dltMessage)))
    }
}