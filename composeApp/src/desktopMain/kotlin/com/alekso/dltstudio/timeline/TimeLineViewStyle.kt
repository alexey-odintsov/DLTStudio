package com.alekso.dltstudio.timeline

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class TimeLineViewStyle(
    val verticalPaddingDp: Dp,
    val fontSize: TextUnit,
    val fontColor: Color,
    val labelBackgroundColor: Color,
    val labelHeight: TextUnit,
    val lineHeight: Dp,
    val lineHeightHighlighted: Dp,
) {
    companion object {
        val Default = TimeLineViewStyle(
            verticalPaddingDp = 10.dp,
            fontSize = 10.sp,
            fontColor = Color.LightGray,
            labelBackgroundColor = Color(0xc0808080),
            labelHeight = TextUnit(12f, TextUnitType.Sp),
            lineHeight = 1.5f.dp,
            lineHeightHighlighted = 2.5f.dp,
        )
    }
}
