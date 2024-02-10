package com.alekso.dltstudio.timeline

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltstudio.ParseSession
import com.alekso.dltstudio.cpu.CPUAnalyzer
import com.alekso.dltstudio.cpu.CPUCLegend
import com.alekso.dltstudio.cpu.CPUSEntry
import com.alekso.dltstudio.cpu.CPUSLegend
import com.alekso.dltstudio.cpu.CPUSView
import com.alekso.dltstudio.cpu.CPUUsageEntry
import com.alekso.dltstudio.cpu.CPUUsageView
import com.alekso.dltstudio.memory.MemoryAnalyzer
import com.alekso.dltstudio.memory.MemoryLegend
import com.alekso.dltstudio.memory.MemoryUsageEntry
import com.alekso.dltstudio.memory.MemoryView
import com.alekso.dltstudio.memory.TimelineAnalyzer
import com.alekso.dltstudio.ui.HorizontalDivider
import com.alekso.dltstudio.ui.ImageButton
import com.alekso.dltstudio.user.UserAnalyzer
import com.alekso.dltstudio.user.UserStateEntry
import com.alekso.dltstudio.user.UserStateLegend
import com.alekso.dltstudio.user.UserStateView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH)
private val LEGEND_WIDTH_DP = 250.dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TimeLinePanel(
    modifier: Modifier,
    dltSession: ParseSession?,
    progressCallback: (Float) -> Unit,
    offsetSec: Float,
    offsetUpdate: (Float) -> Unit,
    scale: Float,
    scaleUpdate: (Float) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    var cursorPosition by remember { mutableStateOf(Offset(0f, 0f)) }
    var secSizePx by remember { mutableStateOf(1f) }

    val dragCallback = { pe: PointerEvent, width: Int ->
        if (dltSession != null) {
            val secSize: Float = width / (dltSession.totalSeconds.toFloat())
            val dragAmount = pe.changes.first().position.x - pe.changes.first().previousPosition.x
            offsetUpdate(offsetSec + (dragAmount / secSize / scale))
        }
    }

    Column(modifier = modifier) {
        Row {
            ImageButton(modifier = Modifier.size(32.dp),
                iconName = "icon_run.xml",
                title = "Analyze timeline",
                onClick = {
                    if (dltSession != null) {
                        coroutineScope.launch {
                            val _cpuUsage = mutableStateListOf<CPUUsageEntry>()
                            val _cpus = mutableStateListOf<CPUSEntry>()
                            val _memt = mutableMapOf<String, MutableList<MemoryUsageEntry>>()
                            val _userState = mutableMapOf<Int, MutableList<UserStateEntry>>()
                            val _userEntries = mutableMapOf<String, MutableList<TimelineEntry>>()

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

                                    analyzeCPUC(message, _cpuUsage, index)
                                    analyzeCPUS(message, _cpus, index)
                                    analyzeMemory(message, index, _memt)
                                    analyzeUserState(message, index, _userState)
                                    progressCallback.invoke((index.toFloat() / dltSession.dltMessages.size))
                                }

                                // todo: should be user defined and stored on user side
                                // todo: try split approach - regexp is too slow
                                val patters = "(?<value>\\d+.\\d+)\\s+%(?<key>(.*)pid\\s*:\\d+)\\("
                                dltSession.dltMessages.forEachIndexed { index, message ->
                                    TimelineAnalyzer.analyzeEntries(
                                        message,
                                        appId = "MON",
                                        contextId = "CPUP",
                                        regex = patters.toRegex(),
                                        map = _userEntries
                                    )
                                    progressCallback.invoke((index.toFloat() / dltSession.dltMessages.size))

                                }
                            }
                            withContext(Dispatchers.Default) {
                                dltSession.cpuUsage.clear()
                                dltSession.cpuUsage.addAll(_cpuUsage)
                                dltSession.cpus.clear()
                                dltSession.cpus.addAll(_cpus)
                                dltSession.memt.clear()
                                dltSession.memt = _memt
                                dltSession.userStateEntries = _userState
                                dltSession.userEntries = _userEntries
                                dltSession.totalSeconds =
                                    (dltSession.timeEnd - dltSession.timeStart).toInt() / 1000
                            }
                        }
                    }
                })
            HorizontalDivider(modifier = Modifier.height(32.dp))

            ImageButton(modifier = Modifier.size(32.dp),
                iconName = "icon_left.xml",
                title = "Move left",
                onClick = { offsetUpdate(offsetSec + 1f) })

            ImageButton(modifier = Modifier.size(32.dp),
                iconName = "icon_right.xml",
                title = "Move right",
                onClick = { offsetUpdate(offsetSec - 1f) })

            ImageButton(modifier = Modifier.size(32.dp),
                iconName = "icon_zoom_in.xml",
                title = "Zoom in",
                onClick = { scaleUpdate(scale + 1f) })

            ImageButton(modifier = Modifier.size(32.dp),
                iconName = "icon_zoom_out.xml",
                title = "Zoom out",
                onClick = { scaleUpdate(scale - 1f) })

            ImageButton(modifier = Modifier.size(32.dp),
                iconName = "icon_fit.xml",
                title = "Fit timeline",
                onClick = {
                    scaleUpdate(1f)
                    offsetUpdate(0f)
                })

        }
        Divider()

        if (dltSession != null) {

            Text(
                "Time range: ${simpleDateFormat.format(dltSession.timeStart)} .. ${
                    simpleDateFormat.format(dltSession.timeEnd)
                }"
            )
            Text("Offset: ${"%.2f".format(offsetSec)}; scale: ${"%.2f".format(scale)}")

            Row {
                Box(modifier = Modifier.width(LEGEND_WIDTH_DP))
                TimeRuler(
                    Modifier.fillMaxWidth(1f),
                    offsetSec,
                    scale,
                    dltSession
                )
            }

            val state = rememberLazyListState()
            val panels = mutableStateListOf<@Composable () -> Unit>(
                {
                    Row {
                        TimelineLegend(
                            modifier = Modifier.width(LEGEND_WIDTH_DP).height(200.dp),
                            title = "CPU usage by process",
                            map = dltSession.userEntries
                        )
                        TimelinePercentageView(
                            offsetSec = offsetSec,
                            scale = scale,
                            modifier = Modifier.height(200.dp).fillMaxWidth()
                                .onPointerEvent(
                                    PointerEventType.Move,
                                    onEvent = { dragCallback(it, size.width) }),
                            map = dltSession.userEntries,
                            dltSession = dltSession
                        )
                    }
                },
                {
                    Row {
                        UserStateLegend(
                            modifier = Modifier.width(LEGEND_WIDTH_DP).height(100.dp),
                            map = dltSession.userStateEntries
                        )
                        UserStateView(
                            offset = offsetSec,
                            scale = scale,
                            modifier = Modifier.height(100.dp).fillMaxWidth()
                                .onPointerEvent(
                                    PointerEventType.Move,
                                    onEvent = { dragCallback(it, size.width) }),
                            map = dltSession.userStateEntries,
                            dltSession = dltSession
                        )
                    }
                },
                {
                    Row {
                        CPUCLegend(
                            modifier = Modifier.width(LEGEND_WIDTH_DP).height(300.dp),
                            items = dltSession.cpuUsage
                        )
                        CPUUsageView(
                            offset = offsetSec,
                            scale = scale,
                            modifier = Modifier.height(300.dp).fillMaxWidth()
                                .onPointerEvent(
                                    PointerEventType.Move,
                                    onEvent = { dragCallback(it, size.width) }),
                            items = dltSession.cpuUsage,
                            dltSession = dltSession
                        )
                    }
                },
                {
                    Row {
                        CPUSLegend(
                            modifier = Modifier.width(LEGEND_WIDTH_DP).height(300.dp),
                            items = dltSession.cpus
                        )
                        CPUSView(
                            offset = offsetSec,
                            scale = scale,
                            modifier = Modifier.height(300.dp).fillMaxWidth()
                                .onPointerEvent(
                                    PointerEventType.Move,
                                    onEvent = { dragCallback(it, size.width) }),
                            items = dltSession.cpus,
                            dltSession = dltSession
                        )
                    }
                },
                {
                    Row {
                        MemoryLegend(
                            modifier = Modifier.width(LEGEND_WIDTH_DP).height(300.dp),
                            map = dltSession.memt
                        )
                        MemoryView(
                            modifier = Modifier.height(300.dp).fillMaxWidth()
                                .onPointerEvent(
                                    PointerEventType.Move,
                                    onEvent = { dragCallback(it, size.width) }),

                            map = dltSession.memt,
                            offset = offsetSec,
                            scale = scale,
                            dltSession = dltSession
                        )
                    }
                }
            )
            Box(modifier = Modifier.weight(1f)) {
                LazyColumn(Modifier.onPointerEvent(
                    eventType = PointerEventType.Move,
                    onEvent = { event ->
                        cursorPosition = event.changes[0].position
                    }).onSizeChanged {
                    secSizePx = (it.width * scale) / (dltSession.totalSeconds)
                }, state
                ) {
                    if (dltSession != null) {
                        items(panels.size) { i ->
                            if (i > 0) {
                                Divider(Modifier.padding(2.dp))
                            }
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
                Canvas(modifier = modifier.fillMaxSize().clipToBounds()) {
                    if (cursorPosition.x < LEGEND_WIDTH_DP.toPx()) return@Canvas

                    drawLine(
                        Color(0xFFFFFFc0),
                        Offset(cursorPosition.x, 0f),
                        Offset(cursorPosition.x, size.height)
                    )
                }
            }
        }
    }
}

private fun analyzeCPUC(
    message: DLTMessage,
    _cpuUsage: SnapshotStateList<CPUUsageEntry>,
    index: Int
) {
    if (message.ecuId == "MGUA" && message.extendedHeader?.applicationId?.startsWith(
            "MON"
        ) == true &&
        message.extendedHeader?.contextId == "CPUC"
    ) {
        try {
            _cpuUsage.add(CPUAnalyzer.analyzeCPUUsage(index, message))
        } catch (e: Exception) {
            // skip
        }
    }
}

private fun analyzeCPUS(
    message: DLTMessage,
    _cpus: SnapshotStateList<CPUSEntry>,
    index: Int
) {
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
}

private fun analyzeUserState(
    message: DLTMessage,
    index: Int,
    _userState: MutableMap<Int, MutableList<UserStateEntry>>
) {
    if (message.ecuId == "MGUA"
        && message.extendedHeader?.applicationId?.startsWith("ALD") == true
        && message.extendedHeader?.contextId == "SYST"
        && message.payload?.asText()?.contains("state changed from") == true
    ) {
        try {
            val userState =
                UserAnalyzer.analyzeUserStateChanges(index, message)
            if (!_userState.containsKey(userState.uid)) {
                _userState[userState.uid] = mutableListOf()
            }
            (_userState[userState.uid] as MutableList).add(userState)
        } catch (e: Exception) {
            // skip
        }
    }
}

private fun analyzeMemory(
    message: DLTMessage,
    index: Int,
    _memt: MutableMap<String, MutableList<MemoryUsageEntry>>
) {
    if (message.ecuId == "MGUA" && message.extendedHeader?.applicationId?.startsWith(
            "MON"
        ) == true &&
        message.extendedHeader?.contextId == "MEMT"
    ) {
        try {
            val memt = MemoryAnalyzer.analyzeMemoryUsage(index, message)
            if (!_memt.containsKey(memt.name)) {
                _memt[memt.name] = mutableListOf()
            }
            (_memt[memt.name] as MutableList).add(memt)
        } catch (e: Exception) {
            // skip
        }
    }
}

@Preview
@Composable
fun PreviewTimeline() {
    TimeLinePanel(
        Modifier.fillMaxWidth().height(600.dp),
        dltSession = ParseSession({}, listOf(File(""))),
        progressCallback = { },
        offsetSec = 0f,
        offsetUpdate = {},
        scale = 1f,
        scaleUpdate = {},
    )
}