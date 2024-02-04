package com.alekso.dltstudio.user

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alekso.dltstudio.ParseSession
import com.alekso.dltstudio.colors.ColorPalette
import java.io.File

@Composable
fun UserStateView(
    modifier: Modifier,
    map: Map<Int, List<UserStateEntry>>,
    offset: Float = 0f,
    scale: Float = 1f,
    dltSession: ParseSession
) {
    val textMeasurer = rememberTextMeasurer()

    Canvas(modifier = modifier.background(Color.Gray).clipToBounds()) {
        val height = size.height
        val width = size.width
        val secSize: Float = size.width / (dltSession.totalSeconds * 1.dp.toPx())
        val itemHeight = height / UserState.entries.size.toFloat()
        val topOffset = itemHeight / 3f

        for (i in 0..<UserState.entries.size) {
            val y = i * itemHeight + topOffset
            drawLine(Color.LightGray, Offset(0f, y), Offset(width, y), alpha = 0.5f)
            drawText(
                textMeasurer,
                text = "${UserState.entries[i]}",
                topLeft = Offset(3.dp.toPx(), y),
                style = TextStyle(color = Color.LightGray, fontSize = 10.sp)
            )
        }

        val translationXPx = offset * secSize.dp.toPx()
        val _2dp = 2.dp.toPx()
        map.entries.forEachIndexed { index, key ->
            val items = map.get<Any, List<UserStateEntry>>(key.key)
            items?.forEachIndexed { i, entry ->
                val prev = if (i > 0) items[i - 1] else null

                val prevX = if (prev != null) {
                    ((prev.timestamp - dltSession.timeStart) / 1000 * secSize.dp.toPx())
                } else {
                    0f
                }
                val curX = ((entry.timestamp - dltSession.timeStart) / 1000 * secSize.dp.toPx())

                val curOldY = entry.oldState.ordinal * itemHeight + topOffset
                val curY = entry.newState.ordinal * itemHeight + topOffset

                if (prev != null) {
                    val prevY = prev.newState.ordinal * itemHeight + topOffset
                    drawLine(
                        ColorPalette.getColor(index, 0.5f),
                        Offset(translationXPx * scale + prevX * scale, prevY),
                        Offset(translationXPx * scale + curX * scale, curOldY),
                        strokeWidth = _2dp,
                    )
                }
                drawLine(
                    ColorPalette.getColor(index, 0.5f),
                    Offset(translationXPx * scale + curX * scale, curOldY),
                    Offset(translationXPx * scale + curX * scale, curY),
                    strokeWidth = _2dp,
                )
            }

        }

    }
}

@Preview
@Composable
fun PreviewUserStateView() {
    val userStateEntries = mutableMapOf(
        0 to mutableListOf(
            UserStateEntry(
                index = 150740,
                timestamp = 1705410539667,
                uid = 0,
                oldState = UserState.BOOTING,
                newState = UserState.RUNNING_LOCKED
            ),
            UserStateEntry(
                index = 162146,
                timestamp = 1705410541567,
                uid = 0,
                oldState = UserState.RUNNING_LOCKED,
                newState = UserState.RUNNING_UNLOCKING
            ),
            UserStateEntry(
                index = 162438,
                timestamp = 1705410544739,
                uid = 0,
                oldState = UserState.RUNNING_UNLOCKING,
                newState = UserState.RUNNING_UNLOCKED
            ),
        ),
        10 to mutableListOf(
            UserStateEntry(
                index = 157066,
                timestamp = 1705410540536,
                uid = 10,
                oldState = UserState.BOOTING,
                newState = UserState.RUNNING_LOCKED
            ),
            UserStateEntry(
                index = 165899,
                timestamp = 1705410542435,
                uid = 10,
                oldState = UserState.RUNNING_LOCKED,
                newState = UserState.RUNNING_UNLOCKING
            ),
            UserStateEntry(
                index = 176028,
                timestamp = 1705410544163,
                uid = 10,
                oldState = UserState.RUNNING_UNLOCKING,
                newState = UserState.RUNNING_UNLOCKED
            ),
        )
    )
    val dltSession = ParseSession({}, listOf(File("")))
    dltSession.timeStart = 1705410528156
    dltSession.timeEnd = 1705410544163
    dltSession.totalSeconds = 50

    UserStateView(
        offset = 0f,
        scale = 1f,
        modifier = Modifier.height(300.dp).fillMaxWidth().padding(start = 10.dp),
        map = userStateEntries,
        dltSession = dltSession
    )
}