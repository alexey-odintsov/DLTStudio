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
fun CPUSView(
    modifier: Modifier,
    items: List<CPUSEntry>,
    offset: Float = 0f,
    scale: Float = 1f,
    totalSeconds: Int,
    timeStart: Long,
    timeEnd: Long
) {
    val textMeasurer = rememberTextMeasurer()

    Canvas(modifier = modifier.background(Color.Gray).clipToBounds()) {
        val height = size.height
        val height_1_100 = size.height / 100
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
            val prevX = if (prev != null) {
                ((prev.timestamp - timeStart) / 1000f * secSize.dp.toPx())
            } else {
                0f
            }
            val curX =
                ((entry.timestamp - timeStart) / 1000f * secSize.dp.toPx())

            for (j in 0..<CPUS_ENTRY.entries.size) {
                val prevY = if (prev != null) height - height_1_100 * prev.entry[j] else 0f
                val curY = height - height_1_100 * entry.entry[j]
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
fun PreviewCPUSView() {
    val ts = Instant.now().toEpochMilli()
    val te = ts + 7000
    val totalSeconds = (te - ts).toInt() / 1000

    CPUSView(
        modifier = Modifier.width(200.dp).height(200.dp), items = listOf(
            CPUSEntry(
                0,
                123123213,
                listOf(99.8f, 52.9f, 37.3f, 0f, 3.6f, 1.4f, 4.4f, 0f, 0f, 75.4f, 94.6f, 1.9f)
            ),
            CPUSEntry(
                1,
                123123213,
                listOf(99.6f, 49.3f, 39.4f, 0f, 3.6f, 1.3f, 6f, 0f, 0f, 76.2f, 95.6f, 1.9f)
            ),
        ), timeStart = ts, timeEnd = te, totalSeconds = totalSeconds
    )
}