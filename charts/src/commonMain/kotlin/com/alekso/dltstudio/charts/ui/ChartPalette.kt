package com.alekso.dltstudio.charts.ui

import androidx.compose.ui.graphics.Color
import kotlin.random.Random
import kotlin.random.nextInt

object ChartPalette {
    val colors = mutableListOf<Color>()

    fun getColor(index: Int, excludedColors: List<Color> = emptyList()): Color {
        if (index < colors.size) {
            return colors[index]
        }
        var color: Color
        do {
            color = Color(
                Random.nextInt(0..255),
                Random.nextInt(0..255),
                Random.nextInt(0..255)
            )
        } while (color in colors || color in excludedColors)
        colors.add(index, color)
        return color
    }
}