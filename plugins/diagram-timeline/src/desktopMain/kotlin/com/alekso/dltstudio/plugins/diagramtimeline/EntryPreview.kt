package com.alekso.dltstudio.plugins.diagramtimeline

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.alekso.dltstudio.charts.model.ChartEntry
import com.alekso.dltstudio.charts.model.EventEntry
import com.alekso.dltstudio.model.contract.LogMessage

@Composable
fun EntryPreview(selectedEntry: ChartEntry<LogMessage>?) {
    val logMessage = selectedEntry?.data
    if (logMessage != null) {
        Text("${logMessage.dltMessage.payloadText()}")
    } else {
        Text("No event selected")
    }
}

@Preview
@Composable
fun PreviewEntryPreview() {
    Column {
        EntryPreview(EventEntry(System.currentTimeMillis(), "test"))
    }
}