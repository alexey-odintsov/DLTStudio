package com.alekso.dltstudio.ui.timeline

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alekso.dltstudio.ui.ParseSession
import com.alekso.dltstudio.ui.cpu.CPUAnalyzer
import com.alekso.dltstudio.ui.cpu.CPUSView
import com.alekso.dltstudio.ui.cpu.CPUUsageEntry
import com.alekso.dltstudio.ui.cpu.CPUUsageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH)

@Composable
fun TimeLinePanel(
    modifier: Modifier,
    dltSession: ParseSession?,
    progressCallback: (Float) -> Unit
) {
    var offset by remember { mutableStateOf(0) }
    var scale by remember { mutableStateOf(1f) }

    Column(modifier = modifier) {
        if (dltSession != null) {
            val coroutineScope = rememberCoroutineScope()

            Button(onClick = {
                coroutineScope.launch {
                    val _cpuUsage = mutableStateListOf<CPUUsageEntry>()
                    withContext(Dispatchers.IO) {

                        println("Start Timeline building .. ${dltSession.dltMessages.size} messages")

                        dltSession.dltMessages.forEachIndexed { index, message ->
                            // timeStamps
                            val ts = (message.timeStampSec * 1000L + message.timeStampUs / 1000)
                            if (ts > dltSession.timeEnd) {
                                dltSession.timeEnd = ts
                            }
                            if (ts < dltSession.timeStart) {
                                dltSession.timeStart = ts
                            }


                            // CPUC
                            if (message.ecuId == "MGUA" && message.extendedHeader?.applicationId?.startsWith(
                                    "MON"
                                ) == true &&
                                message.extendedHeader?.contextId == "CPUC"
                            ) {
                                _cpuUsage.add(CPUAnalyzer.analyzeCPUUsage(index, message))
                            }

                            progressCallback.invoke((index.toFloat() / dltSession.dltMessages.size))
                        }

                    }
                    withContext(Dispatchers.Default) {
                        dltSession.cpuUsage.clear()
                        dltSession.cpuUsage.addAll(_cpuUsage)
                    }
                }
            }) {
                Text("Build timeline")
            }

            Text(
                "Time range: ${simpleDateFormat.format(dltSession.timeStart)} .. ${
                    simpleDateFormat.format(dltSession.timeEnd)
                }"
            )

            val state = rememberLazyListState()
            val panels = mutableStateListOf<@Composable () -> Unit>(
                {
                    Row {
                        Box(modifier = Modifier.width(150.dp)) {
                            Text("CPU Usage")
                        }
                        CPUUsageView(
                            offset = offset,
                            scale = scale,
                            modifier = Modifier.height(300.dp).fillMaxWidth(),
                            items = dltSession.cpuUsage
                        )
                    }
                },
                {
                    Row {
                        Box(modifier = Modifier.width(150.dp)) {
                            Text("CPU Usage")
                        }
                        CPUSView(
                            offset = offset,
                            scale = scale,
                            modifier = Modifier.height(300.dp).fillMaxWidth().padding(top = 10.dp),
                            items = dltSession.cpus
                        )
                    }
                },
                {
                    Row {
                        Box(modifier = Modifier.width(150.dp)) {
                            Text("CPU Usage")
                        }
                        CPUUsageView(
                            offset = offset,
                            scale = scale,
                            modifier = Modifier.height(300.dp).fillMaxWidth().padding(top = 10.dp),
                            items = dltSession.cpuUsage
                        )
                    }
                }
            )
            Box(modifier = Modifier.weight(1f)) {
                LazyColumn(Modifier, state) {
                    if (dltSession != null) {
                        items(panels.size) { i ->
                            panels[i].invoke()
                        }
                    }
                }
                VerticalScrollbar(
                    modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                    adapter = rememberScrollbarAdapter(
                        scrollState = state
                    )
                )
            }
        }
    }
}