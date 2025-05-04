package com.alekso.dltstudio.logs.infopanel

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alekso.dltmessage.SampleData
import com.alekso.dltstudio.model.contract.LogMessage
import com.alekso.dltstudio.plugins.contract.PluginLogPreview
import com.alekso.dltstudio.ui.Panel
import com.alekso.dltstudio.uicomponents.TabsPanel

@Composable
fun LogPreviewPanel(
    modifier: Modifier,
    logMessage: LogMessage?,
    previewPanels: SnapshotStateList<PluginLogPreview>,
) {
    if (previewPanels.size < 1) return

    var tabIndex by remember { mutableStateOf(0) }
    val tabClickListener: (Int) -> Unit = { i -> tabIndex = i }

    Column(modifier = modifier.background(MaterialTheme.colors.background)) {
        Panel(Modifier.fillMaxSize(), title = "Message Info") {

            TabsPanel(
                tabIndex,
                previewPanels.map { it.getPanelName() }.toMutableStateList(),
                tabClickListener
            )

            Row(Modifier.weight(1f)) {
                // PluginPanel as this parameter to renderPanel is unstable, so we marked PluginPanel as Stable
                (previewPanels[tabIndex]).renderPreview(
                    modifier = Modifier.weight(1f),
                    logMessage = logMessage,
                )
            }
        }
    }
}


@Preview
@Composable
fun PreviewLogPreview() {
    val dltMessage = LogMessage(SampleData.getSampleDltMessages(1)[0])
    LogPreviewPanel(
        Modifier.width(200.dp),
        logMessage = dltMessage,
        previewPanels = mutableStateListOf(),
    )
}
