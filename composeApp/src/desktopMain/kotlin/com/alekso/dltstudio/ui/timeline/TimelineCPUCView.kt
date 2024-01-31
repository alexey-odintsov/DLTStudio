package com.alekso.dltstudio.ui.timeline

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.alekso.dltstudio.ui.cpu.CPUUsageEntry

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
fun TimelineCPUCView(modifier: Modifier, items: List<CPUUsageEntry>, offset: Int, scale: Float) {
    val textMeasurer = rememberTextMeasurer()

    Canvas(modifier = modifier.background(Color.Gray)) {
        val height = size.height
        val width = size.width

        for (i in 0..100 step 10) {
            drawLine(
                Color.LightGray,
                Offset(0f, height * i / 100f),
                Offset(width, height * i / 100f),
                alpha = 0.5f
            )
            drawText(
                textMeasurer,
                text = "${100 - i}%",
                topLeft = Offset(3.dp.toPx(), height * i / 100f),
                style = TextStyle(color = Color.LightGray)
            )
        }


        items.forEachIndexed { i, entry ->
            val prev = if (i > 0) items[i - 1] else null
            val step = 10.dp.toPx()


            for (j in 0..<entry.cpuUsage.size) {
                val prevX = if (prev != null) (i - 1) * step else 0f
                val prevY =
                    if (prev != null) height - height * prev.cpuUsage[j] / 100f else 0f
                val curX = i * step
                val curY = height - height * entry.cpuUsage[j] / 100f
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
    TimelineCPUCView(
        offset = 0, scale = 1f,
        modifier = Modifier.width(200.dp).height(200.dp), items = listOf(
            CPUUsageEntry(0, 123123213, listOf(60.3f, 50.0f)),
            CPUUsageEntry(13, 123123214, listOf(49.8f, 55.2f)),
            CPUUsageEntry(24, 123123215, listOf(11.3f, 35.2f)),
            CPUUsageEntry(45, 123123216, listOf(8.0f, 50.5f)),
            CPUUsageEntry(68, 123123217, listOf(34.9f, 70.3f)),
            CPUUsageEntry(78, 123123218, listOf(55.1f, 80.4f)),
            CPUUsageEntry(97, 123123219, listOf(80.6f, 96.4f)),
            CPUUsageEntry(105, 123123220, listOf(84.6f, 99.7f)),
            CPUUsageEntry(123, 123123221, listOf(89.6f, 99.9f)),
            CPUUsageEntry(141, 123123222, listOf(94.6f, 81.3f)),
        )
    )
}