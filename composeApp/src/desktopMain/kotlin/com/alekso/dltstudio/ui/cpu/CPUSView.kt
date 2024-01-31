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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
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
    Color(23, 123, 200),
    Color(55, 123, 55),
    Color(23, 123, 172),
    Color(250, 123, 200),
    Color(240, 222, 24),
    Color(34, 34, 234),
)

@Composable
fun CPUSView(modifier: Modifier, items: List<CPUSEntry>) {
    val textMeasurer = rememberTextMeasurer()

    Canvas(modifier = modifier.background(Color.Gray)) {
        val height = size.height
        val height_1_100 = size.height / 100
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

            val prevX = if (prev != null) (i - 1) * step else 0f
            val curX = i * step

            for (j in 0..<CPUS_ENTRY.entries.size) {
                val prevY = if (prev != null) height - height_1_100 * prev.entry[j] else 0f
                val curY = height - height_1_100 * entry.entry[j]
                drawLine(colors[j], Offset(prevX, prevY), Offset(curX, curY))
            }

        }

    }
}

@Preview
@Composable
fun PreviewCPUSView() {
    CPUSView(
        modifier = Modifier.width(200.dp).height(200.dp), items = listOf(
            CPUSEntry(123123213, listOf(99.8f, 52.9f, 37.3f, 0f, 3.6f, 1.4f, 4.4f, 0f, 0f, 75.4f, 94.6f, 1.9f)),
            CPUSEntry(123123213, listOf(99.6f, 49.3f, 39.4f, 0f, 3.6f, 1.3f, 6f, 0f, 0f, 76.2f, 95.6f, 1.9f)),
        )
    )
}