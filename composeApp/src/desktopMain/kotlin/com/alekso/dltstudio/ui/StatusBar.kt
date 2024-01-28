package com.alekso.dltstudio.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.io.File

@Composable
fun StatusBar(modifier: Modifier = Modifier, progress: Float, dltSession: ParseSession?) {
    Row(modifier = modifier.height(30.dp).padding(4.dp)) {
        if (dltSession != null) {
            Text(text = "File: ${dltSession.file.absoluteFile} Messages: ${"%,d".format(dltSession.dltMessages.size)}")
        } else {
            Text(text = "No file loaded")
        }
        if (progress > 0f) {
            Text(" | Progress: %.3f".format(progress * 100f))
            LinearProgressIndicator(
                modifier = Modifier.weight(1f).padding(start = 4.dp, end = 4.dp)
                    .align(Alignment.CenterVertically),
                backgroundColor = Color.White,
                progress = progress,
            )
        }
    }
}

@Preview
@Composable
fun PreviewStatusBarNoSession() {
    StatusBar(Modifier.background(Color.LightGray), 0f, null)
}

@Preview
@Composable
fun PreviewStatusBarInProgress() {
    StatusBar(
        Modifier.background(Color.LightGray),
        0.4f,
        ParseSession({ i -> }, File("/user/test/file.dlt"))
    )
}