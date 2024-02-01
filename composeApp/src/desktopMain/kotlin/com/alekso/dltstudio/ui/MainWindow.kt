package com.alekso.dltstudio.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.alekso.dltstudio.ui.cpu.CPUPanel
import com.alekso.dltstudio.ui.logs.LogsPanel
import com.alekso.dltstudio.ui.timeline.TimeLinePanel

@Composable
@Preview
fun MainWindow() {
    var dltSession by remember { mutableStateOf<ParseSession?>(null) }
    var progress by remember { mutableStateOf(0f) }
    var tabIndex by remember { mutableStateOf(0) }
    var offset by remember { mutableStateOf(0f) }
    var scale by remember { mutableStateOf(1f) }

    val tabClickListener: (Int) -> Unit = { i -> tabIndex = i }
    val statusBarProgressCallback: (Float) -> Unit = { i -> progress = i }
    val newSessionCallback: (ParseSession) -> Unit = { newSession -> dltSession = newSession }
    val offsetUpdateCallback: (Float) -> Unit = { newOffset -> offset = newOffset }
    val scaleUpdateCallback: (Float) -> Unit = { newScale -> scale = newScale }

    Column {

        // TODO: Add toolbox

        TabsPanel(tabIndex, listOf("Logs", "CPU", "Timeline"), tabClickListener)

        when (tabIndex) {
            0 -> LogsPanel(
                modifier = Modifier.weight(1f),
                dltSession,
                newSessionCallback,
                statusBarProgressCallback
            )

            1 -> CPUPanel(
                modifier = Modifier.weight(1f),
                dltSession,
                statusBarProgressCallback
            )

            2 -> TimeLinePanel(
                modifier = Modifier.weight(1f),
                dltSession,
                statusBarProgressCallback,
                offset,
                offsetUpdateCallback,
                scale,
                scaleUpdateCallback
            )
        }
        Divider()
        StatusBar(modifier = Modifier.fillMaxWidth(), progress, dltSession)
    }
}