package com.alekso.dltstudio.ui

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

@Composable
@Preview
fun Cell(
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Left,
    text: String,
    isHeader: Boolean = false
) {
    Text(
        modifier = Modifier
            .padding(end = 1.dp)
            .background(color = if (isHeader) Color.Transparent else Color(250, 250, 250))
            .then(modifier),
        maxLines = 1,
        textAlign = textAlign,
        fontSize = 12.sp,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight(if (isHeader) 600 else 400),
        softWrap = false,
        text = text
    )
}