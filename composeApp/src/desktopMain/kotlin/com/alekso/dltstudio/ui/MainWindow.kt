package com.alekso.dltstudio.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.alekso.dltstudio.ui.cpu.CPUPanel
import com.alekso.dltstudio.ui.logs.LogsPanel

@Composable
@Preview
fun MainWindow() {
    var dltSession by remember { mutableStateOf<ParseSession?>(null) }
    var progress by remember { mutableStateOf(0f) }
    var tabIndex by remember { mutableStateOf(0) }

    val tabClickListener: (Int) -> Unit = { i -> tabIndex = i }
    val statusBarProgressCallback: (Float) -> Unit = { i -> progress = i }
    val newSessionCallback: (ParseSession) -> Unit = { newSession -> dltSession = newSession }

    Column {

        // TODO: Add toolbox

        TabsPanel(tabIndex, listOf("Logs", "CPU", "Memory"), tabClickListener)

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

            2 -> Text("Memory tab content", modifier = Modifier.weight(1f))
        }
        Divider()
        StatusBar(modifier = Modifier.fillMaxWidth(), progress, dltSession)
    }
}