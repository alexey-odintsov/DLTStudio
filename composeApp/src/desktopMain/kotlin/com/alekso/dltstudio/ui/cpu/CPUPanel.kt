package com.alekso.dltstudio.ui.cpu

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
    var cpuUsage = remember { mutableStateListOf<CPUUsageEntry>() }

    Column(modifier = modifier) {
        if (dltSession != null) {
            val coroutineScope = rememberCoroutineScope()

            Button(onClick = {
                coroutineScope.launch {
                    val _cpuLogs = mutableStateListOf<Int>()
                    val _cpuUsage = mutableStateListOf<CPUUsageEntry>()

                    withContext(Dispatchers.IO) {
                        println("Start CPU analysing.. ${dltSession.dltMessages.size} messages")

                        dltSession.dltMessages.forEachIndexed { index, message ->
                            if (message.ecuId == "MGUA" && message.extendedHeader?.applicationId?.startsWith(
                                    "MON"
                                ) == true &&
                                message.extendedHeader?.contextId == "CPUC"
                            ) {
                                _cpuLogs.add(index)
                                _cpuUsage.add(CPUAnalyzer.analyzeCPUUsage(message))
                            }
                            progressCallback.invoke((index.toFloat() / dltSession.dltMessages.size))
                        }

                        println("End CPU analysing, found ${_cpuLogs.size} messages")
                    }
                    withContext(Dispatchers.Default) {
                        cpuLogs.clear()
                        cpuLogs.addAll(_cpuLogs)
                        cpuUsage.clear()
                        cpuUsage.addAll(_cpuUsage)
                    }
                }
            }) {
                Text("Analyze CPU Usage")
            }

            Text("Messages found: ${cpuLogs.size}")
            CPUUsageView(modifier = Modifier.fillMaxSize(1f), items = cpuUsage)
        }
    }
}