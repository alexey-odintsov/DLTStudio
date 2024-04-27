package com.alekso.dltstudio.logs

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class CellStyle(
    val backgroundColor: Color? = null, val textColor: Color? = null
) {
    companion object {
        val Default = CellStyle()
    }
}

@Composable
@Preview
fun Cell(
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Left,
    text: String,
    isHeader: Boolean = false,
    cellStyle: CellStyle? = null,
    wrapContent: Boolean = false,
) {
    val color = if (cellStyle != null) {
        cellStyle.textColor ?: Color.Unspecified
    } else Color.Unspecified

    if (wrapContent) {
        Text(
            modifier = Modifier.padding(end = 1.dp).then(modifier),
            textAlign = textAlign,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight(if (isHeader) 600 else 400),
            softWrap = true,
            text = text,
            color = color,
        )
    } else {
        Text(
            modifier = Modifier.padding(end = 1.dp).height(12.dp).then(modifier),
            maxLines = 1,
            textAlign = textAlign,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight(if (isHeader) 600 else 400),
            text = text,
            color = color,
        )
    }
}