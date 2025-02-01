package com.alekso.dltstudio.files

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.alekso.dltstudio.logs.Cell
import com.alekso.dltstudio.logs.CellDivider
import com.alekso.dltstudio.logs.CellStyle
import com.alekso.dltstudio.ui.CustomButton
import com.alekso.dltstudio.ui.FileChooserDialog
import com.alekso.dltstudio.ui.FileChooserDialogState
import java.io.File

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FilesPanel(
    analyzeState: FilesState,
    files: SnapshotStateList<FileEntry>,
    previewState: State<PreviewState?>,
    onPreviewDialogClosed: () -> Unit,
    onSearchButtonClicked: () -> Unit,
    onSaveFileClicked: (File) -> Unit,
    onFileEntryClicked: (FileEntry) -> Unit,
) {

    when (val state = previewState.value) {
        is TextPreviewState -> {
            TextPreviewDialog(
                visible = true,
                onDialogClosed = onPreviewDialogClosed,
                fileEntry = state.entry,
            )
        }

        is ImagePreviewState -> {
            ImagePreviewDialog(
                visible = true,
                onDialogClosed = onPreviewDialogClosed,
                fileEntry = state.entry,
                imageBitmap = state.imageBitmap,
            )
        }

        is FilePreviewState -> {
            FileChooserDialog(
                dialogContext = FileChooserDialogState.DialogContext.SAVE_FILE,
                fileName = state.entry.name,
                title = "Save file",
                onFileSelected = { file ->
                    if (file != null) {
                        onSaveFileClicked(file)
                    }
                },
            )
        }

        else -> {}
    }

    Column(Modifier.padding(4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CustomButton(
                modifier = Modifier.padding(0.dp),
                onClick = onSearchButtonClicked,
            ) {
                Text(text = if (analyzeState == FilesState.IDLE) "Search for files" else "Stop search")
            }
            if (files.isNotEmpty()) {
                Text(
                    modifier = Modifier.padding(4.dp),
                    text = "${files.size} files found ${files.sumOf { e -> e.size }} bytes"
                )
            }
        }

        if (files.isEmpty()) {
            Text("Files will be shown here..")
        } else {
            val listState = rememberLazyListState()
            Box(Modifier.weight(1f)) {
                LazyColumn(
                    Modifier.fillMaxSize(),
                    state = listState
                ) {
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
                        items = files,
                        key = { _, key -> key },
                        contentType = { _, _ -> FileEntry::class }) { i, fileEntry ->
                        Row(
                            Modifier.combinedClickable(
                                onClick = {},
                                onDoubleClick = { onFileEntryClicked(fileEntry) })
                        ) {
                            FileItem(
                                i = i.toString(),
                                name = fileEntry.name,
                                size = fileEntry.size.toString(),
                                date = fileEntry.creationDate
                            )
                        }
                    }
                }
                VerticalScrollbar(
                    modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                    adapter = rememberScrollbarAdapter(
                        scrollState = listState
                    )
                )
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