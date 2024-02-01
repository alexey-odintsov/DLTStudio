package com.alekso.dltstudio.ui.timeline

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
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

@Composable
fun TimeRuler(
    modifier: Modifier = Modifier,
    offset: Int,
    scale: Float,
    dltSession: ParseSession,
) {


    val textMeasurer = rememberTextMeasurer()

    Canvas(modifier = modifier.height(40.dp).clipToBounds()) {
        val secSize: Float = size.width / (dltSession.totalSeconds * 1.dp.toPx())
        drawText(
            textMeasurer,
            text = "${dltSession.totalSeconds} seconds; Width: ${size.width}; Sec size: $secSize",
            topLeft = Offset(3.dp.toPx(), 0f),
            style = TextStyle(color = Color.Black)
        )
        for (i in 0..dltSession.totalSeconds) {
            drawLine(
                Color.Gray,
                Offset(
                    offset * secSize.dp.toPx() * scale + i * scale * secSize.dp.toPx(),
                    size.height / 2
                ),
                Offset(
                    offset * secSize.dp.toPx() * scale + i * scale * secSize.dp.toPx(),
                    size.height
                ),
            )
        }
    }
}

@Preview
@Composable
fun PreviewTimeRuler() {

}