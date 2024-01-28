package com.alekso.dltstudio.ui.cpu

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.alekso.dltstudio.ui.ParseSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun CPUPanel(modifier: Modifier, dltSession: ParseSession?, progressCallback: (Float) -> Unit) {
    val cpuLogs = remember { mutableStateListOf<Int>() }

    Column(modifier = modifier) {
        if (dltSession != null) {
            val coroutineScope = rememberCoroutineScope()

            Button(onClick = {
                coroutineScope.launch {
                    withContext(Dispatchers.IO) {
                        println("Start CPU analysing.. ${dltSession.dltMessages.size} messages")
                        cpuLogs.clear()
                        dltSession.dltMessages.forEachIndexed { index, message ->
                            if (message.ecuId == "MGUA" && message.extendedHeader?.applicationId?.startsWith("MON") == true &&
                                message.extendedHeader?.contextId == "CPUC") {
                                cpuLogs.add(index)
                            }
                            progressCallback.invoke((index.toFloat() / dltSession.dltMessages.size))
                        }


                        println("End CPU analysing, found ${cpuLogs.size} messages")
                    }
                }
            }) {
                Text("Analyze CPU Usage")
            }

            Text("Messages found: ${cpuLogs.size}")
            Canvas(modifier = Modifier, onDraw = {

            })
        }
    }
}