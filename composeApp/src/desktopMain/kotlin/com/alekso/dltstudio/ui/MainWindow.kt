package com.alekso.dltstudio.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.DragData
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.onExternalDrag
import androidx.compose.ui.unit.dp
import com.alekso.dltparser.dlt.DLTMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(ExperimentalComposeUiApi::class)
@Composable
@Preview
fun MainWindow() {
    var dltSession by remember { mutableStateOf<ParseSession?>(null) }
    var selectedRow by remember { mutableStateOf(0) }
    var progress by remember { mutableStateOf(0f) }
    val coroutineScope = rememberCoroutineScope()

    Column {
        Button(onClick = {
            dltSession =
                ParseSession({ i -> progress = i }, File("/users/alekso/Downloads/dlt3.dlt"))
            coroutineScope.launch {
                withContext(Dispatchers.IO) {
                    dltSession?.start()
                }
            }
        }) {
            Text("Load file..")
        }

        LazyScrollable(
            modifier = Modifier.weight(1f)
                .background(Color.LightGray)
                .onExternalDrag(onDrop = {
                    if (it.dragData is DragData.FilesList) {
                        val filesList = it.dragData as DragData.FilesList
                        val pathList = filesList.readFiles()
                        println(pathList)
                        if (pathList.isNotEmpty()) {
                            // TODO: Add support for multiple files session
                            dltSession =
                                ParseSession({ i -> progress = i }, File(pathList[0].substring(5)))
                            coroutineScope.launch {
                                withContext(Dispatchers.IO) {
                                    dltSession?.start()
                                }
                            }
                        }
                    }
                }),
            dltSession
        ) { i -> selectedRow = i }
        LogPreview(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            dltSession?.dltMessages?.getOrNull(selectedRow)
        )
        Divider()
        StatusBar(modifier = Modifier.fillMaxWidth(), progress, dltSession)
    }
}


@Composable
fun LogPreview(modifier: Modifier, dltMessage: DLTMessage?) {
    Column(modifier = modifier.then(Modifier)) {
        Divider()
        if (dltMessage != null) {
            Text(text = "${dltMessage.standardHeader}")
            Divider()
            Text(text = "${dltMessage.extendedHeader}")
            Divider()
            Text(text = "${dltMessage.payload}")
        }
    }
}

@Composable
fun StatusBar(modifier: Modifier = Modifier, progress: Float, dltSession: ParseSession?) {
    Row(modifier = modifier.height(30.dp).padding(4.dp)) {
        if (dltSession != null) {
            Text(text = "File: ${dltSession.file.absoluteFile} Messages: ${"%,d".format(dltSession.dltMessages.size)}")
        } else {
            Text(text = "No file loaded")
        }
        if (progress > 0f) {
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