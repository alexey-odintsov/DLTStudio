package com.alekso.dltstudio.uicomponents

import androidx.compose.ui.graphics.Color
import kotlin.random.Random
import kotlin.random.nextInt

object ColorPalette {

    private val colors = mutableListOf(
        Color.Blue,
        Color.Red,
        Color.Green,
        Color.Yellow,
        Color.White,
        Color.Cyan,
        Color.Magenta,
        Color.LightGray,
        Color.DarkGray,
        Color.Gray,
        Color.Black
    )

    fun getColor(index: Int, alpha: Float = 1f): Color {
        if (index >= colors.size) {
            // in the upper level index could be increased without calling getColor method.
            for (i in 0..(index - colors.size)) {
                colors.add(
                    // todo: Exclude existing and similar colors
                    Color(
                        Random.nextInt(0..255),
                        Random.nextInt(0..255),
                        Random.nextInt(0..255)
                    )
                )
            }
        }
        return Color(colors[index].red, colors[index].green, colors[index].blue, alpha)
    }
}