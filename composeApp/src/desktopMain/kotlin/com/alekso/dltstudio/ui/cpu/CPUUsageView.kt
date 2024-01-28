package com.alekso.dltstudio.ui.cpu

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val colors = listOf(
    Color.Blue,
    Color.Red,
    Color.Green,
    Color.Yellow,
    Color.White,
    Color.Cyan,
    Color.Magenta,
    Color.LightGray,
    Color.DarkGray,
    Color(23, 123, 200)
)

@Composable
fun CPUUsageView(modifier: Modifier, items: List<CPUUsageEntry>) {
    Canvas(modifier = modifier.background(Color.Gray)) {
        items.forEachIndexed { i, entry ->
            val prev = if (i > 0) items[i - 1] else null
            val step = 10.dp.toPx()


            for (j in 0..<entry.cpuUsage.size) {
                val prevX = if (prev != null) (i - 1) * step else 0f
                val prevY =
                    if (prev != null) size.height - size.height * prev.cpuUsage[j] / 100f else 0f
                val curX = i * step
                val curY = size.height - size.height * entry.cpuUsage[j] / 100f
                drawLine(
                    colors[j],
                    Offset(prevX, prevY),
                    Offset(curX, curY),
                )
            }
        }

    }
}

@Preview
@Composable
fun PreviewCPUUsageView() {
    CPUUsageView(
        modifier = Modifier.width(200.dp).height(200.dp), items = listOf(
            CPUUsageEntry(123123213, listOf(60.3f, 50.0f)),
            CPUUsageEntry(123123214, listOf(49.8f, 55.2f)),
            CPUUsageEntry(123123215, listOf(11.3f, 35.2f)),
            CPUUsageEntry(123123216, listOf(8.0f, 50.5f)),
            CPUUsageEntry(123123217, listOf(34.9f, 70.3f)),
            CPUUsageEntry(123123218, listOf(55.1f, 80.4f)),
            CPUUsageEntry(123123219, listOf(80.6f, 96.4f)),
            CPUUsageEntry(123123220, listOf(84.6f, 99.7f)),
            CPUUsageEntry(123123221, listOf(89.6f, 99.9f)),
            CPUUsageEntry(123123222, listOf(94.6f, 81.3f)),
        )
    )
}