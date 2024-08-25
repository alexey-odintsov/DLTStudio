package com.alekso.dltstudio.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alekso.dltstudio.AppTheme

@Composable
fun StatusBar(modifier: Modifier = Modifier, progress: Float, statusText: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier//.height(30.dp)
            .padding(4.dp)
    ) {
        Text(modifier = Modifier.weight(1f), text = statusText)
        if (progress > 0f) {
            Box(Modifier.width(300.dp)) {
                LinearProgressIndicator(
                    modifier = Modifier.height(10.dp).padding(start = 4.dp, end = 4.dp)
                        .align(Alignment.Center),
                    backgroundColor = Color.White,
                    progress = progress,
                    strokeCap = StrokeCap.Butt
                )
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    fontSize = 9.sp,
                    lineHeight = 10.sp,
                    color = if (progress > 0.5f) Color.White else Color.DarkGray,
                    text = "%.1f%%".format(progress * 100f)
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewStatusBarNoSession() {
    StatusBar(Modifier.background(Color.LightGray), 0f, "File1.dlt")
}

@Preview
@Composable
fun PreviewStatusBarInProgress() {
    AppTheme {
        StatusBar(
            Modifier.background(Color.LightGray),
            0.4f,
            "/user/test/file.dlt"
        )
    }
}