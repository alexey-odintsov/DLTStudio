package com.alekso.dltstudio.logs.infopanel

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.alekso.dltstudio.colors.ColorPalette
import kotlin.math.max
import kotlin.math.min

@Composable
fun VirtualDevicePreview(
    modifier: Modifier = Modifier,
    deviceSize: Size,
    deviceViews: List<DeviceView>?,
) {
    val textMeasurer = rememberTextMeasurer()

    Column(modifier = modifier.background(MaterialTheme.colors.background).fillMaxSize()) {
        deviceViews?.forEachIndexed { i, view ->
            Text(
                modifier = Modifier.padding(start = 2.dp, end = 2.dp),
                text = "$i: ${view.rect} ${view.id}",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
            )
        }
        Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
            Canvas(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {

                val rectStyle = Stroke(width = 2.dp.toPx())

                val scale: Float = min(
                    1f,
                    max(
                        size.width / max(deviceSize.width, 0f),
                        size.height / max(deviceSize.height, 0f)
                    )
                )

                renderVirtualDevice(deviceSize, rectStyle, scale)
                deviceViews?.forEachIndexed { i, view ->
                    val color = ColorPalette.getColor(i)
                    renderRectView(i, view, rectStyle, textMeasurer, color, scale)
                }

                // Debug info
                drawText(
                    size = Size(size.width, 200f),
                    topLeft = Offset(4f, 4f),
                    textMeasurer = textMeasurer,
                    text = "Canvas: $size; scale: $scale",
                    style = TextStyle(color = Color.Blue, fontSize = 10.sp)
                )

            }
        }
    }
}

private fun DrawScope.renderVirtualDevice(
    deviceSize: Size,
    rectStyle: Stroke,
    scale: Float
) {
    drawRect(
        color = Color.LightGray,
        Offset(0f, 0f) * scale,
        size = deviceSize * scale,
        style = Fill
    )
    drawRect(
        color = Color.Gray,
        Offset(0f, 0f) * scale,
        size = deviceSize * scale,
        style = rectStyle
    )
}

private fun DrawScope.renderRectView(
    i: Int,
    view: DeviceView,
    rectStyle: Stroke,
    textMeasurer: TextMeasurer,
    color: Color,
    scale: Float
) {
    val text = view.id ?: "unknown id"
    val textStyle = TextStyle(color = color, fontSize = 10.sp)
    val textSize = textMeasurer.measure(
        text, style = textStyle, constraints = Constraints.fixed(
            width = (view.rect.width * scale).toInt(),
            height = (view.rect.height.toInt() * scale).toInt(),
        )
    ).size
    drawRect(
        color,
        Offset(view.rect.left, view.rect.top) * scale,
        size = view.rect.size * scale,
        style = rectStyle
    )
    drawText(
        size = textSize.toSize(),
        textMeasurer = textMeasurer,
        text = text,
        topLeft = Offset(
            (view.rect.left + view.rect.width / 2f) * scale - textSize.width / 2f,
            (view.rect.top + view.rect.height / 2f) * scale - textSize.height / 2f
        ),
        style = textStyle,
    )
}

@Preview
@Composable
fun PreviewVirtualDevicePreview() {
    VirtualDevicePreview(
        modifier = Modifier.fillMaxSize(),
        deviceSize = Size(600f, 300f),
        deviceViews = listOf(
            DeviceView(Rect(10f, 50f, 295f, 290f), id = "Left rect long name goes here again"),
            DeviceView(Rect(305f, 50f, 590f, 290f)),
        )
    )
}