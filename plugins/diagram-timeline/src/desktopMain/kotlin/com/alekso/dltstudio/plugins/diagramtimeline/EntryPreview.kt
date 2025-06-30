package com.alekso.dltstudio.plugins.diagramtimeline

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.alekso.dltstudio.charts.model.ChartEntry

@Composable
fun entryPreview(selectedEntry: ChartEntry?) {
    Text("${selectedEntry}")
}