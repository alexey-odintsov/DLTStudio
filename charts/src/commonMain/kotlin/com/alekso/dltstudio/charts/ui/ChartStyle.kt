package com.alekso.dltstudio.charts.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ChartStyle(
    val backgroundColor: Color,
    val verticalPadding: Dp,
    val seriesColor: Color,
    val labelTextStyle: TextStyle,
) {
    companion object {
        val Default = ChartStyle(
            backgroundColor = Color.White,
            verticalPadding = 6.dp,
            seriesColor = Color.LightGray,
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
        val Dark = ChartStyle(
            backgroundColor = Color.Gray,
            verticalPadding = 6.dp,
            seriesColor = Color.LightGray,
            labelTextStyle = TextStyle(
                color = Color.LightGray,
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