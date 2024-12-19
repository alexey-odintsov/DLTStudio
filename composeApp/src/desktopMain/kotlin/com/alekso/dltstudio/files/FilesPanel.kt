package com.alekso.dltstudio.files

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.alekso.dltstudio.files.PreviewState.Type
import com.alekso.dltstudio.logs.Cell
import com.alekso.dltstudio.logs.CellDivider
import com.alekso.dltstudio.logs.CellStyle
import com.alekso.dltstudio.model.LogMessage
import com.alekso.dltstudio.ui.CustomButton


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FilesPanel(
    viewModel: FilesViewModel,
    logMessages: List<LogMessage>,
    analyzeState: FilesState,
    files: SnapshotStateMap<Long, FileEntry>,
) {
    var textPreviewDialogState: TextPreviewDialogState? by remember { mutableStateOf(null) }
    var imagePreviewDialogState: ImagePreviewDialogState? by remember { mutableStateOf(null) }


    val devicePreviewsDialogState = remember { mutableStateOf(false) }

    if (devicePreviewsDialogState.value) {
        TextPreviewDialog(
            visible = devicePreviewsDialogState.value,
            onDialogClosed = {
                devicePreviewsDialogState.value = false
            },
            fileEntry = textPreviewDialogState?.fileEntry!!,
        )
    }

    if (imagePreviewDialogState?.showDialog == true && imagePreviewDialogState?.fileEntry != null) {
        ImagePreviewDialog(
            visible = true,
            onDialogClosed = {
                imagePreviewDialogState = null
            },
            fileEntry = imagePreviewDialogState?.fileEntry!!,
            imageBitmap = imagePreviewDialogState?.imageBitmap!!,
        )
    }

    when (viewModel.previewState.value?.type) {

        Type.Text -> {
            textPreviewDialogState =
                TextPreviewDialogState(showDialog = true, fileEntry = viewModel.previewState.value?.entry)
            devicePreviewsDialogState.value = true
        }

        Type.Image -> {
            imagePreviewDialogState =
                ImagePreviewDialogState(
                    showDialog = true,
                    fileEntry = viewModel.previewState.value?.entry,
                    imageBitmap = (viewModel.previewState.value as ImagePreviewState).imageBitmap
                )
        }

        else -> {
        }
    }

    Column(Modifier.padding(4.dp)) {
        CustomButton(
            modifier = Modifier.padding(0.dp),
            onClick = { viewModel.startFilesSearch(logMessages) },
        ) {
            Text(text = if (analyzeState == FilesState.IDLE) "Search for files" else "Stop search")
        }

        if (files.isEmpty()) {
            Text("Files will be shown here..")
        } else {
            LazyColumn(Modifier.weight(1f)) {
                stickyHeader {
                    FileItem(
                        isHeader = true,
                        i = "#",
                        name = "Name",
                        size = "Size",
                        date = "Date created"
                    )
                }
                itemsIndexed(
                    items = files.keys.toList().sorted(),
                    key = { _, key -> key },
                    contentType = { _, _ -> FileEntry::class }
                ) { i, key ->
                    val fileEntry = files[key]
                    if (fileEntry != null) {
                        Row(
                            Modifier.combinedClickable(
                                onClick = {},
                                onDoubleClick = { viewModel.onFileClicked(fileEntry) })
                        ) {
                            FileItem(
                                i = i.toString(),
                                name = fileEntry.name,
                                size = fileEntry.size.toString(),
                                date = fileEntry.creationDate
                            )
                        }
                    } else {
                        Text("$i Empty file!")
                    }
                }
            }
        }
    }
}

private val cellStyle = CellStyle(backgroundColor = Color.White)

@Composable
fun FileItem(i: String, name: String, size: String, date: String, isHeader: Boolean = false) {
    Row(Modifier.background(Color.White)) {
        Cell(
            text = i,
            modifier = Modifier.width(30.dp).padding(2.dp),
            isHeader = isHeader,
            cellStyle = cellStyle
        )
        CellDivider()
        Cell(
            text = name,
            modifier = Modifier.weight(1f).padding(2.dp),
            isHeader = isHeader,
            cellStyle = cellStyle
        )
        CellDivider()
        Cell(
            textAlign = TextAlign.Right,
            text = size,
            modifier = Modifier.width(60.dp).padding(2.dp),
            isHeader = isHeader,
            cellStyle = cellStyle
        )
        CellDivider()
        Cell(
            textAlign = TextAlign.Right,
            text = date,
            modifier = Modifier.width(200.dp).padding(2.dp),
            isHeader = isHeader,
            cellStyle = cellStyle
        )
    }

}