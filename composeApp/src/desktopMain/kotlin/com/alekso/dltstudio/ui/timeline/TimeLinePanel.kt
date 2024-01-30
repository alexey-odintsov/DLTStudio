package com.alekso.dltstudio.ui.timeline

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.alekso.dltstudio.ui.ParseSession
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
    var timeStart by remember { mutableStateOf(Long.MAX_VALUE) }
    var timeEnd by remember { mutableStateOf(Long.MIN_VALUE) }
    var offset by remember { mutableStateOf(0) }

    Column(modifier = modifier) {
        if (dltSession != null) {
            val coroutineScope = rememberCoroutineScope()

            Button(onClick = {
                coroutineScope.launch {
                    withContext(Dispatchers.IO) {
                        println("Start Timeline building .. ${dltSession.dltMessages.size} messages")

                        dltSession.dltMessages.forEachIndexed { index, message ->
                            val ts = (message.timeStampSec * 1000L + message.timeStampUs / 1000)
                            if (ts > timeEnd) {
                                timeEnd = ts
                            }
                            if (ts < timeStart) {
                                timeStart = ts
                            }
                            progressCallback.invoke((index.toFloat() / dltSession.dltMessages.size))
                        }

                    }

                }
            }) {
                Text("Build timeline")
            }

            Text(
                "Time range: ${simpleDateFormat.format(timeStart)} .. ${
                    simpleDateFormat.format(
                        timeEnd
                    )
                }"
            )

        }
    }
}