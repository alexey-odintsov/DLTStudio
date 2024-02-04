package com.alekso.dltstudio.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.IconToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.DragData
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.ExternalDragValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.onExternalDrag
import androidx.compose.ui.unit.dp
import com.alekso.dltstudio.ui.cpu.CPUPanel
import com.alekso.dltstudio.ui.logs.CellColorFilter
import com.alekso.dltstudio.ui.logs.CellStyle
import com.alekso.dltstudio.ui.logs.ColorFilterError
import com.alekso.dltstudio.ui.logs.ColorFilterFatal
import com.alekso.dltstudio.ui.logs.ColorFilterWarn
import com.alekso.dltstudio.ui.logs.LogsPanel
import com.alekso.dltstudio.ui.timeline.TimeLinePanel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import java.io.File
import java.net.URI


@OptIn(ExperimentalComposeUiApi::class, ExperimentalResourceApi::class)
@Composable
@Preview
fun MainWindow() {
    var dltSession by remember { mutableStateOf<ParseSession?>(null) }
    var progress by remember { mutableStateOf(0f) }
    var tabIndex by remember { mutableStateOf(0) }
    var offset by remember { mutableStateOf(0f) }
    var scale by remember { mutableStateOf(1f) }

    // Toolbar state
    var toolbarFatalChecked by remember { mutableStateOf(true) }
    var toolbarErrorChecked by remember { mutableStateOf(true) }
    var toolbarWarningChecked by remember { mutableStateOf(true) }
    var toolbarDTLInfoChecked by remember { mutableStateOf(true) }

    val coroutineScope = rememberCoroutineScope()

    val tabClickListener: (Int) -> Unit = { i -> tabIndex = i }
    val statusBarProgressCallback: (Float) -> Unit = { i -> progress = i }
    val newSessionCallback: (ParseSession) -> Unit = { newSession -> dltSession = newSession }
    val offsetUpdateCallback: (Float) -> Unit = { newOffset -> offset = newOffset }
    val scaleUpdateCallback: (Float) -> Unit = { newScale -> scale = newScale }

    val onDropCallback: (ExternalDragValue) -> Unit = {
        if (it.dragData is DragData.FilesList) {
            val filesList = it.dragData as DragData.FilesList
            val pathList = filesList.readFiles()
            println(pathList)
            if (pathList.isNotEmpty()) {
                dltSession = ParseSession(
                    statusBarProgressCallback,
                    pathList.map { path -> File(URI.create(path.substring(5)).path) }
                )
                newSessionCallback.invoke(dltSession!!)
                coroutineScope.launch {
                    withContext(Dispatchers.IO) {
                        dltSession?.start()
                    }
                }
            }
        }
    }

    // todo: User color filters goes here.
    val colorFilters = listOf(
        CellColorFilter(
            { msg -> msg.extendedHeader?.applicationId.equals("VSIP") },
            CellStyle(backgroundColor = Color.Green)
        ),
    )

    Column(modifier = Modifier.onExternalDrag(onDrop = onDropCallback)) {
        // Toolbar
        Row {
            IconToggleButton(modifier = Modifier.size(32.dp),
                checked = toolbarFatalChecked,
                onCheckedChange = { toolbarFatalChecked = it }) {
                Image(
                    painterResource("icon_f.xml"),
                    contentDescription = "Enable fatal logs highlight",
                    modifier = Modifier.padding(6.dp),
                    colorFilter = if (toolbarFatalChecked) ColorFilter.tint(Color.Red) else ColorFilter.tint(
                        Color.Gray
                    )
                )
            }
            IconToggleButton(modifier = Modifier.size(32.dp),
                checked = toolbarErrorChecked,
                onCheckedChange = { toolbarErrorChecked = it }) {
                Image(
                    painterResource("icon_e.xml"),
                    contentDescription = "Enable error logs highlight",
                    modifier = Modifier.padding(6.dp),
                    colorFilter = if (toolbarErrorChecked) ColorFilter.tint(Color.Red) else ColorFilter.tint(
                        Color.Gray
                    )
                )
            }
            IconToggleButton(modifier = Modifier.size(32.dp),
                checked = toolbarWarningChecked,
                onCheckedChange = { toolbarWarningChecked = it }) {
                Image(
                    painterResource("icon_w.xml"),
                    contentDescription = "Enable warning logs highlight",
                    modifier = Modifier.padding(6.dp),
                    colorFilter = if (toolbarWarningChecked)
                        ColorFilter.tint(
                            Color(0xE7, 0x62, 0x29)
                        ) else ColorFilter.tint(Color.Gray)
                )
            }
            HorizontalDivider(modifier = Modifier.height(IntrinsicSize.Max))
            IconToggleButton(modifier = Modifier.size(32.dp),
                checked = toolbarDTLInfoChecked,
                onCheckedChange = { toolbarDTLInfoChecked = it }) {
                Image(
                    painterResource("icon_dlt_info.xml"),
                    contentDescription = "Show log preview panel",
                    modifier = Modifier.padding(6.dp),
                    colorFilter = if (toolbarDTLInfoChecked)
                        ColorFilter.tint(Color.Blue) else ColorFilter.tint(Color.Gray)
                )
            }
        }

        val mergedFilters = mutableListOf<CellColorFilter>()
        mergedFilters.addAll(colorFilters)
        if (toolbarWarningChecked) {
            mergedFilters.add(ColorFilterWarn)
        }
        if (toolbarErrorChecked) {
            mergedFilters.add(ColorFilterError)
        }
        if (toolbarFatalChecked) {
            mergedFilters.add(ColorFilterFatal)
        }


        TabsPanel(tabIndex, listOf("Logs", "CPU", "Timeline"), tabClickListener)

        when (tabIndex) {
            0 -> LogsPanel(
                modifier = Modifier.weight(1f),
                dltSession,
                mergedFilters,
                toolbarDTLInfoChecked
            )

            1 -> CPUPanel(modifier = Modifier.weight(1f), dltSession, statusBarProgressCallback)
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

@Preview
@Composable
fun PreviewMainWindow() {
    Box(modifier = Modifier.width(400.dp).height(500.dp).background(Color.Yellow)) {
        MainWindow()
    }
}