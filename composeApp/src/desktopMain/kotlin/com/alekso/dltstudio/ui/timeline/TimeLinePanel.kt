package com.alekso.dltstudio.ui.timeline

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alekso.dltstudio.ui.ParseSession
import com.alekso.dltstudio.ui.cpu.CPUAnalyzer
import com.alekso.dltstudio.ui.cpu.CPUUsageEntry
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

            TimelineCPUCView(
                offset = offset, scale = scale,
                modifier = Modifier.height(300.dp).fillMaxWidth(), items = dltSession.cpuUsage
            )
        }
    }
}