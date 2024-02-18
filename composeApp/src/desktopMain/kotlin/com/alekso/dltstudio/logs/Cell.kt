package com.alekso.dltstudio.logs

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
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
import com.alekso.dltstudio.serialization.ColorSerializer
import kotlinx.serialization.Serializable

@Serializable
data class CellStyle(
    @Serializable(with = ColorSerializer::class)
    val backgroundColor: Color? = null,
    @Serializable(with = ColorSerializer::class)
    val textColor: Color? = null
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
    cellStyle: CellStyle? = null
) {
    Text(
        modifier = Modifier
            .padding(end = 1.dp)
            .background(
                if (isHeader) {
                    Color.Transparent
                } else if (cellStyle != null) {
                    cellStyle.backgroundColor ?: Color(250, 250, 250)
                } else {
                    Color.White
                }
            )
            .then(modifier),
        maxLines = 1,
        textAlign = textAlign,
        fontSize = 10.sp,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight(if (isHeader) 600 else 400),
        softWrap = false,
        text = text,
        color = if (cellStyle != null) {
            cellStyle.textColor ?: Color.Unspecified
        } else Color.Unspecified
    )
}