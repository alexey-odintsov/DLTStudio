package com.alekso.dltstudio.charts.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

data class ChartStyle(
    val backgroundColor: Color,
    val labelTextStyle: TextStyle,
) {
    companion object {
        val Default = ChartStyle(
            backgroundColor = Color.White,
            labelTextStyle = TextStyle(
                color = Color.Black,
                fontSize = 10.sp,
                textAlign = TextAlign.End,
                lineHeightStyle = LineHeightStyle(
                    LineHeightStyle.Alignment.Center,
                    LineHeightStyle.Trim.None
                )
            )
        )
    }
}