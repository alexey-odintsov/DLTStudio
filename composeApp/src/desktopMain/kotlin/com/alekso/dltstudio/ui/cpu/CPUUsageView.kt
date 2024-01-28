package com.alekso.dltstudio.ui.cpu

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.unit.dp

@Composable
fun CPUUsageView(modifier: Modifier, items: List<CPUUsageEntry>) {
    Canvas(modifier = modifier.background(Color.Gray)) {
//        scale(scaleX = 10f, scaleY = 15f) {
            items.forEachIndexed { i, entry ->

                //drawLine(Color.Blue, Offset(i * 2.dp.toPx(), size.height - size.height * entry.cpuUsage[0] / 100f),)
                drawRect(
                    Color.Blue,
                    Offset(i * 2.dp.toPx(), size.height - size.height * entry.cpuUsage[0] / 100f),
                    Size(2.dp.toPx(), size.height * entry.cpuUsage[0] / 100f)
                )
            }

//        }
    }
}

@Preview
@Composable
fun PreviewCPUUsageView() {
    CPUUsageView(
        modifier = Modifier.width(200.dp).height(200.dp), items = listOf(
            CPUUsageEntry(123123213, listOf(60.3f, 50.0f)),
            CPUUsageEntry(123123213, listOf(49.8f, 55.2f)),
            CPUUsageEntry(123123213, listOf(11.3f, 35.2f)),
            CPUUsageEntry(123123213, listOf(8.0f, 50.5f)),
            CPUUsageEntry(123123213, listOf(34.9f, 70.3f)),
            CPUUsageEntry(123123213, listOf(55.1f, 80.4f)),
            CPUUsageEntry(123123213, listOf(80.6f, 96.4f)),
        )
    )
}