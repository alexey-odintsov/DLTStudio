package com.alekso.dltstudio.ui.timeline

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun TimeRuler(
    modifier: Modifier = Modifier,
    timeStart: Long,
    timeEnd: Long,
    offset: Int,
    scale: Float
) {
    Canvas(modifier = modifier.height(40.dp)) {
        for (i in 0..100 step 10) {
            drawLine(
                Color.LightGray,
                Offset(offset * scale + i * scale, size.height / 2),
                Offset(offset * scale + i * scale, size.height),
            )
        }
    }
}

@Preview
@Composable
fun PreviewTimeRuler() {

}