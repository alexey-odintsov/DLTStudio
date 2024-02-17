package com.alekso.dltstudio.colors

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.alekso.dltstudio.ui.CustomButton

@Composable
fun ColorPicker() {

    val color by remember { mutableStateOf(Color.Green) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Canvas(modifier = Modifier.size(100.dp)) {
            drawRect(color, Offset(0f, 0f), size)
        }

        Spacer(Modifier.height(4.dp))
        Canvas(modifier = Modifier.fillMaxWidth().height(40.dp)) {
            var hue = 0f
            val hueData = IntArray(size.width.toInt())
            for (i in 0..<size.width.toInt()) {
                drawLine(
                    Color.hsv(hue, saturation = 1f, value = 1f, alpha = 1f),
                    start = Offset(i.toFloat(), 0f),
                    end = Offset(i.toFloat(), size.height)
                )
                hueData[i] = hue.toInt()
                hue += 360f / size.width.toInt()
                if (hue > 360f) hue = 360f
            }
        }

        Spacer(Modifier.height(4.dp))
        Canvas(modifier = Modifier.size(100.dp)) {
            drawRect(color, Offset(0f, 0f), size)
        }

        Spacer(Modifier.height(4.dp))
        CustomButton(
            onClick = {},
        ) {
            Text("Select")
        }
    }
}


@Preview
@Composable
fun PreviewColorPicker() {
    Column {
        ColorPicker()
    }
}