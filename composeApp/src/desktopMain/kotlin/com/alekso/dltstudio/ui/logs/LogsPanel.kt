package com.alekso.dltstudio.ui.logs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.DragData
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.onExternalDrag
import androidx.compose.ui.unit.dp
import com.alekso.dltparser.dlt.MessageInfo
import com.alekso.dltstudio.ui.ParseSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LogsPanel(
    modifier: Modifier = Modifier,
    dltSession: ParseSession?,
    newSessionCallback: (ParseSession) -> Unit,
    progressCallback: (Float) -> Unit
) {
    var selectedRow by remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()
    var dltSession2: ParseSession? = dltSession

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
        }, CellStyle(backgroundColor = Color.Red, textColor = Color.White)),
        CellColorFilter({ msg ->
            msg.extendedHeader?.messageInfo?.messageTypeInfo == MessageInfo.MESSAGE_TYPE_INFO.DLT_LOG_WARN
        }, CellStyle(backgroundColor = Color.Yellow)),
    )

    LazyScrollable(
        modifier = modifier
            .background(Color.LightGray)
            .onExternalDrag(onDrop = {
                if (it.dragData is DragData.FilesList) {
                    val filesList = it.dragData as DragData.FilesList
                    val pathList = filesList.readFiles()
                    println(pathList)
                    if (pathList.isNotEmpty()) {
                        // TODO: Add support for multiple files session
                        dltSession2 =
                            ParseSession(
                                progressCallback,
                                File(pathList[0].substring(5))
                            )
                        newSessionCallback.invoke(dltSession2!!)
                        coroutineScope.launch {
                            withContext(Dispatchers.IO) {
                                dltSession2?.start()
                            }
                        }
                    }
                }
            }),
        dltSession,
        colorFilters
    ) { i -> selectedRow = i }
    LogPreview(
        modifier = Modifier.fillMaxWidth().height(200.dp),
        dltSession?.dltMessages?.getOrNull(selectedRow)
    )
}