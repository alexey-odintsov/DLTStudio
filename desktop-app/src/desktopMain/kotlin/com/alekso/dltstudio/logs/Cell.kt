package com.alekso.dltstudio.logs

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alekso.dltstudio.LocalSettingsUI
import com.alekso.dltstudio.theme.AppTheme
import com.alekso.dltstudio.utils.ColorSerializer
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
    cellStyle: CellStyle? = null,
    wrapContent: Boolean = false,
    isComment: Boolean = false,
    content: (@Composable () -> Unit)? = null,
) {
    val color = if (cellStyle != null) {
        cellStyle.textColor ?: AppTheme.colors.onLogRow
    } else AppTheme.colors.onLogRow

    if (content != null) {
        Box(modifier = modifier) {
            content()
        }
    } else {
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
                    color = AppTheme.colors.onLogRow,
                )
            }
        } else {
            if (wrapContent) {
                Text(
                    modifier = Modifier.padding(end = 1.dp).then(modifier),
                    textAlign = textAlign,
                    fontSize = LocalSettingsUI.current.fontSize.sp,
                    lineHeight = LocalSettingsUI.current.lineHeight.sp,
                    fontFamily = LocalSettingsUI.current.fontFamily,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight(if (isHeader) 600 else 400),
                    softWrap = true,
                    text = text,
                    color = color,
                )
            } else {
                Text(
                    modifier = Modifier.padding(end = 1.dp).then(modifier),
                    maxLines = 1,
                    textAlign = textAlign,
                    fontSize = LocalSettingsUI.current.fontSize.sp,
                    lineHeight = LocalSettingsUI.current.lineHeight.sp,
                    fontFamily = LocalSettingsUI.current.fontFamily,
                    fontStyle = FontStyle.Normal,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight(if (isHeader) 600 else 400),
                    text = text,
                    color = color,
                )
            }
        }
    }
}