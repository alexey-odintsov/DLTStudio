package com.alekso.dltstudio.uicomponents.dialogs

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogState
import androidx.compose.ui.window.DialogWindow
import kotlin.math.roundToInt

private val RecommendedColors = listOf(
    Color(0xFFE0F7FA), Color(0xFFB2EBF2), Color(0xFF80DEEA), Color(0xFF4DD0E1),
    Color(0xFFD1E4F3), Color(0xFFBBDEFB), Color(0xFF90CAF9), Color(0xFF64B5F6),
    Color(0xFFD6F1D6), Color(0xFFC8E6C9), Color(0xFFA5D6A7), Color(0xFF81C784),
    Color(0xFFFFFACD), Color(0xFFFFF59D), Color(0xFFFFF176), Color(0xFFFFEE58)
)

private val RecentColors = mutableListOf<Color>(
    Color(0xFFFFFFFF),
    Color(0xFF000000)
)

@Composable
fun ColorPickerDialog(
    visible: Boolean,
    onDialogClosed: () -> Unit,
    initialColor: Color,
    onColorUpdate: (Color) -> Unit,
) {
    DialogWindow(
        visible = visible,
        onCloseRequest = onDialogClosed,
        title = "Select Color",
        state = DialogState(width = 446.dp, height = 500.dp),
        resizable = false
    ) {
        ColorPicker(
            initialColor = initialColor,
            onColorSelected = onColorUpdate
        )
    }
}

@Composable
private fun ColorPicker(
    initialColor: Color,
    onColorSelected: (Color) -> Unit
) {
    val recentColors by remember { mutableStateOf(RecentColors) }

    val hsv = remember {
        val (h, s, v) = initialColor.toHsv()
        mutableStateOf(Triple(h, s, v))
    }
    var alpha by remember { mutableStateOf(initialColor.alpha) }

    val activeColor = remember(hsv.value, alpha) {
        val (h, s, v) = hsv.value
        Color.hsv(h, s, v, alpha)
    }

    var hexValue by remember { mutableStateOf("") }
    var rValue by remember { mutableStateOf("") }
    var gValue by remember { mutableStateOf("") }
    var bValue by remember { mutableStateOf("") }
    var aValue by remember { mutableStateOf("") }

    LaunchedEffect(activeColor) {
        hexValue = activeColor.toHexCode()
        rValue = (activeColor.red * 255).roundToInt().toString()
        gValue = (activeColor.green * 255).roundToInt().toString()
        bValue = (activeColor.blue * 255).roundToInt().toString()
        aValue = (activeColor.alpha * 255).roundToInt().toString()
    }

    val updateColor: (Color) -> Unit = { color ->
        val (h, s, v) = color.toHsv()
        hsv.value = Triple(h, s, v)
        alpha = color.alpha
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Column(
                    modifier = Modifier.width(250.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SaturationValuePicker(
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        hue = hsv.value.first,
                        saturation = hsv.value.second,
                        value = hsv.value.third,
                        onSaturationValueChanged = { s, v ->
                            hsv.value = hsv.value.copy(second = s, third = v)
                        }
                    )

                    Text("Recent Colors", style = MaterialTheme.typography.titleSmall)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(recentColors) { color ->
                            ColorSwatch(color = color, size = 32.dp, onClick = { updateColor(color) })
                        }
                    }

                    Text("Recommended Colors", style = MaterialTheme.typography.titleSmall)
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(RecommendedColors) { color ->
                            Box(
                                modifier = Modifier
                                    .height(28.dp)
                                    .fillMaxWidth()
                                    .background(color, RoundedCornerShape(4.dp))
                                    .border(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .clickable { updateColor(color) }
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier.width(180.dp).fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ColorPreview(color = activeColor)
                    HueSlider(
                        hue = hsv.value.first,
                        onHueChanged = { newHue ->
                            hsv.value = hsv.value.copy(first = newHue)
                        }
                    )
                    AlphaSlider(
                        alpha = alpha,
                        activeColor = activeColor,
                        onAlphaChanged = { newAlpha -> alpha = newAlpha }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ColorInputField("HEX", hexValue) { newHex ->
                        hexValue = newHex
                        Color.fromHex(newHex)?.let { updateColor(it) }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ColorInputField("R", rValue, Modifier.weight(1f)) { r ->
                            rValue = r
                            r.toIntOrNull()?.let {
                                updateColor(activeColor.copy(red = (it / 255f).coerceIn(0f, 1f)))
                            }
                        }
                        ColorInputField("G", gValue, Modifier.weight(1f)) { g ->
                            gValue = g
                            g.toIntOrNull()?.let {
                                updateColor(activeColor.copy(green = (it / 255f).coerceIn(0f, 1f)))
                            }
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ColorInputField("B", bValue, Modifier.weight(1f)) { b ->
                            bValue = b
                            b.toIntOrNull()?.let {
                                updateColor(activeColor.copy(blue = (it / 255f).coerceIn(0f, 1f)))
                            }
                        }
                        ColorInputField("A", aValue, Modifier.weight(1f)) { a ->
                            aValue = a
                            a.toIntOrNull()?.let {
                                alpha = (it / 255f).coerceIn(0f, 1f)
                            }
                        }
                    }

                    Spacer(Modifier.weight(1f))

                    Button(
                        onClick = {
                            if (recentColors.contains(activeColor)) {
                                RecentColors.remove(activeColor)
                                RecentColors.add(0, activeColor)
                            } else {
                                RecentColors.add(0, activeColor)
                                if (RecentColors.size > 8) {
                                    RecentColors.removeLast()
                                }
                            }
                            onColorSelected(activeColor)
                        },
                        modifier = Modifier
                            .defaultMinSize(minWidth = 100.dp, minHeight = 36.dp)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(4.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp)
                    ) {
                        Text(
                            text = "Select",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorSwatch(
    color: Color,
    modifier: Modifier = Modifier,
    size: Dp? = null,
    onClick: () -> Unit
) {
    val sizeModifier = if (size != null) Modifier.size(size) else modifier
    Box(
        modifier = sizeModifier
            .aspectRatio(1f, size == null)
            .background(color, RoundedCornerShape(4.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                shape = RoundedCornerShape(4.dp)
            )
            .clickable(onClick = onClick)
    )
}

@Composable
private fun ColorPreview(color: Color) {
    val checkerboardColor = MaterialTheme.colorScheme.surfaceVariant
    val checkerboardBrush = remember(checkerboardColor) {
        ShaderBrush(
            ImageShader(
                createCheckerboardImage(checkerboardColor),
                TileMode.Repeated,
                TileMode.Repeated
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(checkerboardBrush)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp))
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(color)
        )
    }
}

@Composable
private fun SaturationValuePicker(
    modifier: Modifier = Modifier,
    hue: Float,
    saturation: Float,
    value: Float,
    onSaturationValueChanged: (Float, Float) -> Unit
) {
    var canvasSize by remember { mutableStateOf(Size(0f, 0f)) }
    val pointerModifier = Modifier.pointerInput(canvasSize) {
        if (canvasSize.width == 0f) return@pointerInput

        detectDragGestures(
            onDragStart = { offset ->
                val newSaturation = (offset.x / canvasSize.width).coerceIn(0f, 1f)
                val newValue = 1 - (offset.y / canvasSize.height).coerceIn(0f, 1f)
                onSaturationValueChanged(newSaturation, newValue)
            },
            onDrag = { change, _ ->
                val newSaturation = (change.position.x / canvasSize.width).coerceIn(0f, 1f)
                val newValue = 1 - (change.position.y / canvasSize.height).coerceIn(0f, 1f)
                onSaturationValueChanged(newSaturation, newValue)
            }
        )
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .onSizeChanged { canvasSize = Size(it.width.toFloat(), it.height.toFloat()) }
            .then(pointerModifier)
    ) {
        val saturationBrush = remember(hue) {
            Brush.horizontalGradient(
                colors = listOf(Color.White, Color.hsv(hue, 1f, 1f))
            )
        }
        val valueBrush = remember {
            Brush.verticalGradient(colors = listOf(Color.Transparent, Color.Black))
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(brush = saturationBrush)
            drawRect(brush = valueBrush)
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            val indicatorX = (saturation * size.width).coerceIn(0f, size.width)
            val indicatorY = ((1 - value) * size.height).coerceIn(0f, size.height)
            val indicatorColor = if (value > 0.5f) Color.Black else Color.White
            drawCircle(
                color = indicatorColor,
                radius = 8.dp.toPx(),
                center = Offset(indicatorX, indicatorY),
                style = Stroke(width = 2.dp.toPx())
            )
        }
    }
}


@Composable
private fun HueSlider(hue: Float, onHueChanged: (Float) -> Unit) {
    ColorSlider(
        value = hue,
        onValueChanged = onHueChanged,
        range = 0f..360f,
        brush = Brush.horizontalGradient(
            colors = listOf(
                Color.Red, Color.Yellow, Color.Green, Color.Cyan, Color.Blue, Color.Magenta, Color.Red
            )
        )
    )
}

@Composable
private fun AlphaSlider(alpha: Float, activeColor: Color, onAlphaChanged: (Float) -> Unit) {
    val checkerboardColor = MaterialTheme.colorScheme.surfaceVariant
    val checkerboardBrush = remember(checkerboardColor) {
        ShaderBrush(
            ImageShader(
                createCheckerboardImage(checkerboardColor),
                TileMode.Repeated,
                TileMode.Repeated
            )
        )
    }
    val alphaBrush = remember(activeColor) {
        Brush.horizontalGradient(colors = listOf(activeColor.copy(alpha = 0f), activeColor.copy(alpha = 1f)))
    }

    Box {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .clip(CircleShape)
                .background(checkerboardBrush)
        )
        ColorSlider(
            value = alpha,
            onValueChanged = onAlphaChanged,
            range = 0f..1f,
            brush = alphaBrush
        )
    }
}

@Composable
private fun ColorSlider(
    value: Float,
    onValueChanged: (Float) -> Unit,
    range: ClosedFloatingPointRange<Float>,
    brush: Brush,
    sliderHeight: Dp = 24.dp
) {
    var containerSize by remember { mutableStateOf(Size.Zero) }
    val thumbColor = MaterialTheme.colorScheme.onSurface

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(sliderHeight)
            .clip(CircleShape)
            .background(brush)
            .onSizeChanged { containerSize = Size(it.width.toFloat(), it.height.toFloat()) }
            .pointerInput(range, containerSize) {
                if (containerSize.width == 0f) return@pointerInput
                detectDragGestures { change, _ ->
                    val position = change.position.x.coerceIn(0f, containerSize.width)
                    val newValue = (position / containerSize.width) * (range.endInclusive - range.start) + range.start
                    onValueChanged(newValue.coerceIn(range))
                }
            },
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .offset {
                    val thumbRadiusPx = sliderHeight.toPx() / 2
                    val travelableWidthPx = containerSize.width - sliderHeight.toPx()
                    val thumbPositionRatio = (value - range.start) / (range.endInclusive - range.start)
                    val thumbPositionPx = (thumbPositionRatio * travelableWidthPx)

                    IntOffset(x = thumbPositionPx.roundToInt(), y = 0)
                }
                .size(sliderHeight)
                .background(Color.White, CircleShape)
                .border(2.dp, thumbColor, CircleShape)
        )
    }
}


@Composable
private fun ColorInputField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier,
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(fontSize = 12.sp),
        shape = RoundedCornerShape(8.dp)
    )
}

private fun createCheckerboardImage(color: Color): ImageBitmap {
    val size = 20
    val image = ImageBitmap(size, size, ImageBitmapConfig.Argb8888)
    val canvas = Canvas(image)
    val paint = Paint().apply { this.color = Color.White }
    canvas.drawRect(Rect(0f, 0f, size.toFloat(), size.toFloat()), paint)
    paint.color = color
    canvas.drawRect(Rect(0f, 0f, size / 2f, size / 2f), paint)
    canvas.drawRect(Rect(size / 2f, size / 2f, size.toFloat(), size.toFloat()), paint)
    return image
}

private fun Color.toHsv(): Triple<Float, Float, Float> {
    val r = red
    val g = green
    val b = blue
    val max = maxOf(r, g, b)
    val min = minOf(r, g, b)
    val delta = max - min

    val h = when {
        max == min -> 0f
        max == r -> 60 * (((g - b) / delta) % 6)
        max == g -> 60 * (((b - r) / delta) + 2)
        else -> 60 * (((r - g) / delta) + 4)
    }.let { if (it < 0) it + 360 else it }

    val s = if (max == 0f) 0f else delta / max
    val v = max

    return Triple(if (h.isNaN()) 0f else h, s, v)
}

private fun Color.toHexCode(): String {
    return String.format(
        "#%02X%02X%02X",
        (red * 255).toInt(),
        (green * 255).toInt(),
        (blue * 255).toInt()
    )
}

private fun Color.Companion.fromHex(hex: String): Color? {
    return try {
        val cleanHex = hex.removePrefix("#")
        val colorInt = cleanHex.toLong(16).toInt()
        if (cleanHex.length == 6) {
            Color(colorInt or 0xFF000000.toInt())
        } else null
    } catch (e: Exception) {
        null
    }
}

@Preview
@Composable
fun PreviewColorPicker() {
    MaterialTheme {
        ColorPicker(Color.Blue) {}
    }
}