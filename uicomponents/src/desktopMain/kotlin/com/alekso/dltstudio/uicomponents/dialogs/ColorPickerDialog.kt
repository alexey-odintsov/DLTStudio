package com.alekso.dltstudio.uicomponents.dialogs

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogState
import androidx.compose.ui.window.DialogWindow
import com.alekso.dltstudio.uicomponents.CustomButton

@Composable
fun ColorPickerDialog(
    visible: Boolean,
    onDialogClosed: () -> Unit,
    initialColor: Color,
    onColorUpdate: (Color) -> Unit,
) {
    DialogWindow(
        visible = visible, onCloseRequest = onDialogClosed,
        title = "Color Picker",
        state = DialogState(width = 300.dp, height = 320.dp)
    ) {
        ColorPicker(initialColor, onColorUpdate)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ColorPicker(initialColor: Color, onColorUpdate: (Color) -> Unit) {
    val hsv = rgbaToHsv(initialColor)
    var selectedHue by remember { mutableStateOf(hsv[0]) }
    var selectedSaturation by remember { mutableStateOf(hsv[1]) }
    var selectedBrightness by remember { mutableStateOf(hsv[2]) }

    Column(
        Modifier.padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Canvas(
            modifier = Modifier.size(100.dp).onPointerEvent(
                eventType = PointerEventType.Press,
                onEvent = { event ->
                    val rvCursorPosition = event.changes[0].position
                    selectedSaturation = rvCursorPosition.x / size.width
                    selectedBrightness = rvCursorPosition.y / size.height
                })
        ) {
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

            drawCircle(
                Color.Blue,
                radius = 10f,
                center = Offset(selectedSaturation * size.width, selectedBrightness * size.height),
                style = Stroke(width = 2.dp.toPx())
            )
        }

        Spacer(Modifier.height(4.dp))
        Canvas(
            modifier = Modifier.fillMaxWidth().height(40.dp)
                .onPointerEvent(
                    eventType = PointerEventType.Press,
                    onEvent = { event ->
                        val cursorPosition = event.changes[0].position
                        selectedHue = cursorPosition.x * 360f / size.width
                    })
        ) {
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

            drawRect(
                Color.Blue,
                topLeft = Offset(selectedHue / 360f * size.width - 2.dp.toPx(), 0f),
                size = Size(4.dp.toPx(), size.height),
                style = Stroke(width = 2.dp.toPx())
            )
        }

        Spacer(Modifier.height(4.dp))
        Canvas(modifier = Modifier.size(100.dp).border(1.dp, Color(223, 223, 223))) {
            drawRect(
                Color.hsv(selectedHue, selectedSaturation, selectedBrightness),
                Offset(0f, 0f), size
            )
        }

        Spacer(Modifier.height(4.dp))
        CustomButton(
            onClick = { onColorUpdate(Color.hsv(selectedHue, selectedSaturation, selectedBrightness)) },
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
    val diff = max - min

    val h: Float = when (max) {
        min -> 0f
        r -> (60 * ((g - b) / diff) + 360) % 360
        g -> (60 * ((b - r) / diff) + 120) % 360
        b -> (60 * ((r - g) / diff) + 240) % 360
        else -> 0f
    }

    val s = if (max == 0f) 0f else diff / max
    val v = max

    return listOf(h, s, v)
}

fun rgbaToHsl(color: Color): List<Float> {
    val r = color.red
    val g = color.green
    val b = color.blue

    val min: Float = minOf(r, g, b)
    val max: Float = maxOf(r, g, b)
    val diff = max - min

    val h: Float = when (max) {
        min -> 0f
        r -> ((60 * (g - b) / diff) + 360) % 360
        g -> (60 * (b - r) / diff) + 120
        b -> (60 * (r - g) / diff) + 240
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
        ColorPicker(Color(.5f, .8f, 1f), {})
    }
}