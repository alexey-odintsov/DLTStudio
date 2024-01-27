package com.alekso.dltstudio.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@Composable
@Preview
fun MainWindow() {
    var dltSession by remember { mutableStateOf<ParseSession?>(null) }
    var progress by remember { mutableStateOf(0f) }
    val coroutineScope = rememberCoroutineScope()

    Column {
        Button(onClick = {
            coroutineScope.launch {
                withContext(Dispatchers.IO) {
                    dltSession = ParseSession(
                        { i -> progress = i },
                        File("/users/alekso/Downloads/dlt3.dlt")
                    )
                    dltSession?.start()
                }
            }
        }) {
            Text("Load file..")
        }

        LazyScrollable(modifier = Modifier.weight(1f).background(Color.LightGray), dltSession)
        StatusBar(modifier = Modifier.fillMaxWidth(), progress)
    }
}

@Preview
@Composable
fun StatusBar(modifier: Modifier = Modifier, progress: Float) {
    LinearProgressIndicator(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color.White,
        progress = progress,
    )
    Box(modifier = modifier) {
        Text(text = "Status")
    }
}