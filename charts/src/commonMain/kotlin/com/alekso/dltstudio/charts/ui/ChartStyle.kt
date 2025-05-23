package com.alekso.dltstudio.charts.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ChartStyle(
    val isDark: Boolean,
    val backgroundColor: Color,
    val verticalPadding: Dp,
    val seriesColor: Color,
    val lineWidth: Dp,
    val labelTextStyle: TextStyle,
    val highlightColor: Color,
) {
    companion object {
        val Default = ChartStyle(
            isDark = false,
            backgroundColor = Color.White,
            verticalPadding = 6.dp,
            seriesColor = Color.LightGray,
            highlightColor = Color.Green,
            lineWidth = 2.dp,
            labelTextStyle = TextStyle(
                color = Color.Black,
                background = Color.White.copy(alpha = 0.75f),
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Start,
                lineHeightStyle = LineHeightStyle(
                    LineHeightStyle.Alignment.Center,
                    LineHeightStyle.Trim.None
                )
            ),
        )
        val Dark = ChartStyle(
            isDark = true,
            backgroundColor = Color.Gray,
            verticalPadding = 6.dp,
            seriesColor = Color.LightGray,
            highlightColor = Color.Green,
            lineWidth = 2.dp,
            labelTextStyle = TextStyle(
                color = Color.LightGray,
                background = Color.Gray.copy(alpha = 0.75f),
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.End,
                lineHeightStyle = LineHeightStyle(
                    LineHeightStyle.Alignment.Center,
                    LineHeightStyle.Trim.None
                )
            )
        )
    }
}