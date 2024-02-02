package com.alekso.dltstudio.ui.memory

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
fun MemoryView(
    modifier: Modifier,
    map: Map<String, List<MemoryUsageEntry>>,
    offset: Float = 0f,
    scale: Float = 1f,
    dltSession: ParseSession
) {
    val textMeasurer = rememberTextMeasurer()

    Canvas(modifier = modifier.background(Color.Gray).clipToBounds()) {
        val height = size.height
        val height_1_100 = size.height / 1000
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

        map.entries.forEach { key ->
            val items = map.get<Any, List<MemoryUsageEntry>>(key.key)
            items?.forEachIndexed { i, entry ->
                if (i == 0) return@forEachIndexed
                val prev = if (i > 0) items[i - 1] else null
                val prevX = if (prev != null) {
                    ((prev.timestamp - dltSession.timeStart) / 1000 * secSize.dp.toPx())
                } else {
                    0f
                }
                val curX = ((entry.timestamp - dltSession.timeStart) / 1000 * secSize.dp.toPx())


                val prevY = if (prev != null) height - height_1_100 * prev.maxRSS else 0f
                val curY = height - height_1_100 * entry.maxRSS
                drawLine(
                    colors[i],
                    Offset(offset * secSize.dp.toPx() * scale + prevX * scale, prevY),
                    Offset(offset * secSize.dp.toPx() * scale + curX * scale, curY),
                )
                println("${offset * secSize.dp.toPx() * scale + prevX * scale}, $prevY -> ${offset * secSize.dp.toPx() * scale + curX * scale}, $curY")
            }

        }

    }
}