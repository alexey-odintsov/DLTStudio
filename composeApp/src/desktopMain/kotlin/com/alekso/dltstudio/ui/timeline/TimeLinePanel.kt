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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.dp
import com.alekso.dltstudio.ui.ParseSession
import com.alekso.dltstudio.ui.cpu.CPUAnalyzer
import com.alekso.dltstudio.ui.cpu.CPUSEntry
import com.alekso.dltstudio.ui.cpu.CPUSView
import com.alekso.dltstudio.ui.cpu.CPUUsageEntry
import com.alekso.dltstudio.ui.cpu.CPUUsageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TimeLinePanel(
    modifier: Modifier,
    dltSession: ParseSession?,
    progressCallback: (Float) -> Unit,
    offset: Float,
    offsetUpdate: (Float) -> Unit,
    scale: Float,
    scaleUpdate: (Float) -> Unit,
) {

    Column(modifier = modifier) {
        if (dltSession != null) {
            val coroutineScope = rememberCoroutineScope()

            Button(onClick = {
                coroutineScope.launch {
                    val _cpuUsage = mutableStateListOf<CPUUsageEntry>()
                    val _cpus = mutableStateListOf<CPUSEntry>()
                    withContext(Dispatchers.IO) {

                        println("Start Timeline building .. ${dltSession.dltMessages.size} messages")

                        dltSession.dltMessages.forEachIndexed { index, message ->
                            // timeStamps
                            val ts = message.getTimeStamp()
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

                            // CPUS
                            if (message.ecuId == "MGUA" && message.extendedHeader?.applicationId?.startsWith(
                                    "MON"
                                ) == true &&
                                message.extendedHeader?.contextId == "CPUS"
                            ) {
                                try {
                                    _cpus.add(CPUAnalyzer.analyzeCPUS(index, message))
                                } catch (e: Exception) {
                                    // skip
                                }
                            }
                            progressCallback.invoke((index.toFloat() / dltSession.dltMessages.size))
                        }

                    }
                    withContext(Dispatchers.Default) {
                        dltSession.cpuUsage.clear()
                        dltSession.cpuUsage.addAll(_cpuUsage)
                        dltSession.cpus.clear()
                        dltSession.cpus.addAll(_cpus)
                        dltSession.totalSeconds =
                            (dltSession.timeEnd - dltSession.timeStart).toInt() / 1000
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
            Text(
                "Offset: ${"%.2f".format(offset)}; scale: ${"%.2f".format(scale)}"
            )
            Row {
                val buttonModifier = Modifier.padding(start = 4.dp, end = 4.dp)
                Button(
                    modifier = buttonModifier,
                    onClick = { offsetUpdate(offset + 1f) }) {
                    Text("<")
                }
                Button(
                    modifier = buttonModifier,
                    onClick = { offsetUpdate(offset - 1f) }) {
                    Text(">")
                }
                Button(
                    modifier = buttonModifier,
                    onClick = { scaleUpdate(scale - 1f) }) {
                    Text("-")
                }
                Button(
                    modifier = buttonModifier,
                    onClick = { scaleUpdate(scale + 1f) }) {
                    Text("+")
                }
                Button(modifier = buttonModifier, onClick = {
                    scaleUpdate(1f)
                    offsetUpdate(0f)
                }) {
                    Text("Reset")
                }
            }

            Row {
                Box(modifier = Modifier.width(150.dp))
                TimeRuler(
                    Modifier.fillMaxWidth(1f),
                    offset,
                    scale,
                    dltSession
                )
            }

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
                            modifier = Modifier.height(300.dp).fillMaxWidth()
                                .onPointerEvent(PointerEventType.Move) {
                                    val secSize: Float =
                                        size.width / (dltSession.totalSeconds * 1.dp.toPx())
                                    val dragAmount =
                                        it.changes.first().position.x - it.changes.first().previousPosition.x
                                    offsetUpdate(offset + (dragAmount / secSize))
                                    println("Drag: $dragAmount")
                                },
                            items = dltSession.cpuUsage,
                            dltSession = dltSession
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
                            items = dltSession.cpus,
                            dltSession = dltSession
                        )
                    }
                },
                {
                    Row {
                        Box(modifier = Modifier.width(150.dp)) {
                            Text("CPU Usage")
                        }
                        CPUUsageView(
                            modifier = Modifier.height(300.dp).fillMaxWidth().padding(top = 10.dp),
                            items = dltSession.cpuUsage,
                            offset = offset,
                            scale = scale,
                            dltSession = dltSession
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