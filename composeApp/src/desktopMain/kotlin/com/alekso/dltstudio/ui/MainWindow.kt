package com.alekso.dltstudio.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
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
import androidx.compose.ui.onExternalDrag
import androidx.compose.ui.unit.dp
import com.alekso.dltparser.dlt.MessageInfo
import com.alekso.dltstudio.ui.cpu.CPUPanel
import com.alekso.dltstudio.ui.logs.CellColorFilter
import com.alekso.dltstudio.ui.logs.CellStyle
import com.alekso.dltstudio.ui.logs.LogsPanel
import com.alekso.dltstudio.ui.timeline.TimeLinePanel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URI


@OptIn(ExperimentalComposeUiApi::class)
@Composable
@Preview
fun MainWindow() {
    var dltSession by remember { mutableStateOf<ParseSession?>(null) }
    var progress by remember { mutableStateOf(0f) }
    var tabIndex by remember { mutableStateOf(0) }
    var offset by remember { mutableStateOf(0f) }
    var scale by remember { mutableStateOf(1f) }
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
                // TODO: Add support for multiple files session
                dltSession = ParseSession(
                    statusBarProgressCallback,
                    File(URI.create(pathList[0].substring(5)).path)
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

    // Add sample color filters
    val colorFilters = listOf(
        CellColorFilter(
            { msg -> msg.extendedHeader?.applicationId.equals("VSIP") },
            CellStyle(backgroundColor = Color.Green)
        ),
        CellColorFilter({ msg ->
            with(msg.extendedHeader?.messageInfo?.messageTypeInfo) {
                this == MessageInfo.MESSAGE_TYPE_INFO.DLT_LOG_FATAL || this == MessageInfo.MESSAGE_TYPE_INFO.DLT_LOG_DLT_ERROR
            }
        }, CellStyle(backgroundColor = Color(0xE7, 0x62, 0x29), textColor = Color.White)),
        CellColorFilter({ msg ->
            msg.extendedHeader?.messageInfo?.messageTypeInfo == MessageInfo.MESSAGE_TYPE_INFO.DLT_LOG_WARN
        }, CellStyle(backgroundColor = Color.Yellow)),
    )


    Column(modifier = Modifier.onExternalDrag(onDrop = onDropCallback)) {
        // TODO: Add toolbox
        TabsPanel(tabIndex, listOf("Logs", "CPU", "Timeline"), tabClickListener)

        when (tabIndex) {
            0 -> LogsPanel(
                modifier = Modifier.weight(1f),
                dltSession,
                colorFilters,
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