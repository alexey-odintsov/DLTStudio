package com.alekso.dltstudio.cpu

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.alekso.dltstudio.colors.ColorPalette
import java.time.Instant


@Composable
fun CPUUsageView(
    modifier: Modifier,
    items: List<CPUUsageEntry>,
    offset: Float = 0f,
    scale: Float = 1f,
    totalSeconds: Int,
    timeStart: Long,
    timeEnd: Long
) {
    val textMeasurer = rememberTextMeasurer()

    Canvas(modifier = modifier.background(Color.Gray).clipToBounds()) {
        val height = size.height
        val width = size.width
        val secSize: Float = size.width / (totalSeconds * 1.dp.toPx())

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

            for (j in 0..<entry.cpuUsage.size) {
                val prevX = if (prev != null) {
                    ((prev.timestamp - timeStart) / 1000000f * secSize.dp.toPx())
                } else {
                    0f
                }
                val prevY = if (prev != null) {
                    height - height * prev.cpuUsage[j] / 100f
                } else {
                    0f
                }
                val curX =
                    ((entry.timestamp - timeStart) / 1000000f * secSize.dp.toPx())
                val curY = height - height * entry.cpuUsage[j] / 100f
                drawLine(
                    ColorPalette.getColor(j),
                    Offset(offset * secSize.dp.toPx() * scale + prevX * scale, prevY),
                    Offset(offset * secSize.dp.toPx() * scale + curX * scale, curY),
                )
            }
        }

    }
}

@Preview
@Composable
fun PreviewCPUUsageView() {
    val ts = Instant.now().toEpochMilli() * 1000L
    val te = ts + 7000000L
    val totalSeconds = (te - ts).toInt() / 1000000

    CPUUsageView(
        modifier = Modifier.width(200.dp).height(200.dp), items = listOf(
            CPUUsageEntry(23, ts + 1450000, listOf(60.3f, 50.0f)),
            CPUUsageEntry(54, ts + 1550000, listOf(49.8f, 55.2f)),
            CPUUsageEntry(76, ts + 2500000, listOf(11.3f, 35.2f)),
            CPUUsageEntry(91, ts + 3000000, listOf(8.0f, 50.5f)),
            CPUUsageEntry(107, ts + 3200000, listOf(34.9f, 70.3f)),
            CPUUsageEntry(167, ts + 3911123, listOf(55.1f, 80.4f)),
            CPUUsageEntry(197, ts + 4040000, listOf(80.6f, 96.4f)),
            CPUUsageEntry(212, ts + 4040004, listOf(84.6f, 99.7f)),
            CPUUsageEntry(247, ts + 5000000, listOf(89.6f, 99.9f)),
            CPUUsageEntry(287, ts + 7000000, listOf(94.6f, 81.3f)),
        ), timeStart = ts, timeEnd = te, totalSeconds = totalSeconds
    )
}