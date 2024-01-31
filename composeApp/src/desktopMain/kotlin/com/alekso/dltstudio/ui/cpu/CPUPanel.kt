package com.alekso.dltstudio.ui.cpu

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alekso.dltstudio.ui.ParseSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Composable
fun CPUPanel(modifier: Modifier, dltSession: ParseSession?, progressCallback: (Float) -> Unit) {
    var cpuUsage = remember { mutableStateListOf<CPUUsageEntry>() }
    var cpus = remember { mutableStateListOf<CPUSEntry>() }

    Column(modifier = modifier) {
        if (dltSession != null) {
            val coroutineScope = rememberCoroutineScope()

            Button(onClick = {
                coroutineScope.launch {
                    val _cpuLogs = mutableStateListOf<Int>()
                    val _cpuUsage = mutableStateListOf<CPUUsageEntry>()
                    val _cpus = mutableStateListOf<CPUSEntry>()

                    withContext(Dispatchers.IO) {
                        println("Start CPU analysing.. ${dltSession.dltMessages.size} messages")

                        dltSession.dltMessages.forEachIndexed { index, message ->
                            // CPUC
                            if (message.ecuId == "MGUA" && message.extendedHeader?.applicationId?.startsWith(
                                    "MON"
                                ) == true &&
                                message.extendedHeader?.contextId == "CPUC"
                            ) {
                                _cpuUsage.add(CPUAnalyzer.analyzeCPUUsage(index, message))
                            }

                            // CPUS
                            if (message.ecuId == "MGUA" && message.extendedHeader?.applicationId?.startsWith(
                                    "MON"
                                ) == true &&
                                message.extendedHeader?.contextId == "CPUS"
                            ) {
                                try {
                                    _cpus.add(CPUAnalyzer.analyzeCPUS(message))
                                } catch (e: Exception) {
                                    // skip
                                }
                            }
                            progressCallback.invoke((index.toFloat() / dltSession.dltMessages.size))
                        }

                        println("End CPU analysing, found ${_cpuLogs.size} messages")
                    }
                    withContext(Dispatchers.Default) {
                        cpuUsage.clear()
                        cpuUsage.addAll(_cpuUsage)
                        cpus.clear()
                        cpus.addAll(_cpus)
                    }
                }
            }) {
                Text("Analyze CPU Usage")
            }

            Text("Messages found: ${cpuUsage.size}")
            CPUUsageView(modifier = Modifier.height(300.dp).fillMaxWidth(), items = cpuUsage)
            CPUSView(
                modifier = Modifier.height(300.dp).fillMaxWidth().padding(top = 10.dp),
                items = cpus
            )
        }
    }
}