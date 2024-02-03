package com.alekso.dltstudio.ui.cpu

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
import com.alekso.dltstudio.ui.ParseSession
import com.alekso.dltstudio.ui.colors.ColorPalette
import java.io.File


@Composable
fun CPUUsageView(
    modifier: Modifier,
    items: List<CPUUsageEntry>,
    offset: Float = 0f,
    scale: Float = 1f,
    dltSession: ParseSession
) {
    val textMeasurer = rememberTextMeasurer()

    Canvas(modifier = modifier.background(Color.Gray).clipToBounds()) {
        val height = size.height
        val width = size.width
        val secSize: Float = size.width / (dltSession.totalSeconds * 1.dp.toPx())

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
                    ((prev.timestamp - dltSession.timeStart) / 1000 * secSize.dp.toPx())
                } else {
                    0f
                }
                val prevY = if (prev != null) {
                    height - height * prev.cpuUsage[j] / 100f
                } else {
                    0f
                }
                val curX =
                    ((entry.timestamp - dltSession.timeStart) / 1000 * secSize.dp.toPx())
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
    CPUUsageView(
        modifier = Modifier.width(200.dp).height(200.dp), items = listOf(
            CPUUsageEntry(23, 123123213, listOf(60.3f, 50.0f)),
            CPUUsageEntry(54, 123123214, listOf(49.8f, 55.2f)),
            CPUUsageEntry(76, 123123215, listOf(11.3f, 35.2f)),
            CPUUsageEntry(91, 123123216, listOf(8.0f, 50.5f)),
            CPUUsageEntry(107, 123123217, listOf(34.9f, 70.3f)),
            CPUUsageEntry(167, 123123218, listOf(55.1f, 80.4f)),
            CPUUsageEntry(197, 123123219, listOf(80.6f, 96.4f)),
            CPUUsageEntry(212, 123123220, listOf(84.6f, 99.7f)),
            CPUUsageEntry(247, 123123221, listOf(89.6f, 99.9f)),
            CPUUsageEntry(287, 123123222, listOf(94.6f, 81.3f)),
        ), dltSession = ParseSession({}, listOf(File("")))
    )
}