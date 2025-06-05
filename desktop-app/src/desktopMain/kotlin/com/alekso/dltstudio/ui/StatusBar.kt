package com.alekso.dltstudio.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alekso.dltstudio.AppTheme

@Composable
fun StatusBar(modifier: Modifier = Modifier, progress: Float, statusText: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(4.dp)
    ) {
        Text(
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            text = statusText,
            fontSize = MaterialTheme.typography.labelMedium.fontSize
        )
        if (progress > 0f) {
            Box(Modifier.width(300.dp)) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.height(10.dp).padding(start = 4.dp, end = 4.dp)
                        .align(Alignment.Center),
                    strokeCap = StrokeCap.Round,
                    drawStopIndicator = {},
                    gapSize = 0.dp,
                )
                val textColor = if (progress > 0.4f) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurface
                Text(
                    modifier = Modifier.align(Alignment.Center).padding(horizontal = 2.dp),
                    fontSize = 9.sp,
                    lineHeight = 10.sp,
                    color = textColor,
                    text = "%.1f%%".format(progress * 100f)
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewStatusBarInProgress() {
    val modifier = Modifier
    val text = "/user/test/file.dlt"
    Box(Modifier.fillMaxSize().background(Color.Black)) {
        Column {
            AppTheme(darkTheme = false) {
                Column {
                    StatusBar(modifier, 0f, text)
                    StatusBar(modifier, 0.15f, text)
                    StatusBar(modifier, 0.45f, text)
                    StatusBar(modifier, 0.5f, text)
                    StatusBar(modifier, 0.6f, text)
                    StatusBar(Modifier, 1f, text)
                }
            }
            AppTheme(darkTheme = true) {
                Column {
                    StatusBar(modifier, 0f, text)
                    StatusBar(modifier, 0.15f, text)
                    StatusBar(modifier, 0.45f, text)
                    StatusBar(modifier, 0.5f, text)
                    StatusBar(modifier, 0.6f, text)
                    StatusBar(Modifier, 1f, text)
                }
            }
        }
    }
}