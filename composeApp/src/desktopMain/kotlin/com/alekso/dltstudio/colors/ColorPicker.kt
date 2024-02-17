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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.alekso.dltstudio.ui.CustomButton

@Composable
fun ColorPicker(initialColor: Color, onColorUpdate: (Color) -> Unit) {

    val color by remember { mutableStateOf(initialColor) }
    val hsv = rgbaToHsv(color)
    val selectedHue = hsv[0]
    val selectedSaturation = hsv[1]
    val selectedBrightness = hsv[2]

    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Canvas(modifier = Modifier.size(100.dp)) {
            for (x in 0..<size.width.toInt()) {
                val saturation = x / size.width
                for (y in 0..<size.height.toInt()) {
                    val brightness = y / size.height
                    drawRect(
                        Color.hsv(
                            selectedHue,
                            saturation = saturation,
                            value = brightness,
                            alpha = 1f
                        ),
                        Offset(x.toFloat(), y.toFloat()),
                        size = Size(1f, 1f)
                    )
                }
            }
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
            drawRect(
                Color.hsl(selectedHue, selectedSaturation, selectedBrightness),
                Offset(0f, 0f), size
            )
        }

        Spacer(Modifier.height(4.dp))
        CustomButton(
            onClick = { onColorUpdate(color) },
        ) {
            Text("Select")
        }
    }
}


fun rgbaToHsv(color: Color): List<Float> {
    val r = color.red
    val g = color.green
    val b = color.blue

    val min: Float = minOf(r, g, b)
    val max: Float = maxOf(r, g, b)

    val h: Float = when (max) {
        min -> 0f
        r -> ((60 * (g - b) / (max - min)) + 360) % 360
        g -> (60 * (b - r) / (max - min)) + 120
        b -> (60 * (r - g) / (max - min)) + 240
        else -> 0f
    }

    val l = (max + min) / 2f

    val s =
        if (max == min) 0f
        else if (l <= .5f) (max - min) / (max + min)
        else (max - min) / (2 - max - min)

    return listOf(h, s, l)
}

@Preview
@Composable
fun PreviewColorPicker() {
    Column {
        ColorPicker(Color.Blue, {})
    }
}