package com.alekso.dltstudio.uicomponents.table

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TableTextCell(
    modifier: Modifier = Modifier,
    text: String,
    textAlign: TextAlign = TextAlign.Left,
    isHeader: Boolean = false,
    backgroundColor: Color? = null,
    textColor: Color? = null,
    wrapContent: Boolean = false,
    isComment: Boolean = false,
) {
    val textColor = textColor ?: Color.Unspecified
    if (isComment) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(start = 4.dp, end = 4.dp, top = 2.dp, bottom = 2.dp)
                    .background(Color.Blue, shape = RoundedCornerShape(3.dp))
                    .padding(horizontal = 4.dp, vertical = 2.dp)
                    .drawBehind {
                        val path = Path()
                        path.moveTo(10f, 0f)
                        path.lineTo(20f, -10f)
                        path.lineTo(30f, 0f)
                        drawPath(path, Color.Blue)
                        path.close()
                    },
                textAlign = textAlign,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight(400),
                softWrap = true,
                text = text,
                color = Color.White,
            )
        }
    } else {
        if (wrapContent) {
            Text(
                modifier = Modifier.padding(end = 1.dp).then(modifier),
                textAlign = textAlign,
//                fontSize = LocalLogsTextStyle.current.fontSize,
//                lineHeight = LocalLogsTextStyle.current.lineHeight,
//                fontFamily = LocalLogsTextStyle.current.fontFamily,
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight(if (isHeader) 600 else 400),
                softWrap = true,
                text = text,
                color = textColor,
            )
        } else {
            Text(
                modifier = Modifier.padding(end = 1.dp).then(modifier),
                maxLines = 1,
                textAlign = textAlign,
//                fontSize = LocalLogsTextStyle.current.fontSize,
//                lineHeight = LocalLogsTextStyle.current.lineHeight,
//                fontFamily = LocalLogsTextStyle.current.fontFamily,
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight(if (isHeader) 600 else 400),
                text = text,
                color = textColor,
            )
        }
    }
}