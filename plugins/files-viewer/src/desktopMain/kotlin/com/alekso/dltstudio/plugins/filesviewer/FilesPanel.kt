package com.alekso.dltstudio.plugins.filesviewer

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.alekso.dltstudio.uicomponents.CustomButton
import com.alekso.dltstudio.uicomponents.table.TableDivider
import com.alekso.dltstudio.uicomponents.table.TableTextCell

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FilesPanel(
    analyzeState: FilesState,
    files: SnapshotStateList<FileEntry>,
    previewState: State<PreviewState?>,
    onPreviewDialogClosed: () -> Unit,
    onSearchButtonClicked: () -> Unit,
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
                    text = "${files.size} files found, total ${LocalFormatter.current.formatSizeHuman(files.sumOf { e -> e.size })}"
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
                        FileItem(
                            Modifier.combinedClickable(
                                onClick = {},
                                onDoubleClick = { onFileEntryClicked(fileEntry) }),
                            i = i.toString(),
                            name = fileEntry.name,
                            size = LocalFormatter.current.formatSizeHuman(fileEntry.size),
                            date = fileEntry.creationDate
                        )
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

@Composable
fun FileItem(
    modifier: Modifier = Modifier,
    i: String, name: String, size: String, date: String, isHeader: Boolean = false
) {
    Row(
        modifier.background(Color(0xFFEEEEEE))
            .padding(bottom = 1.dp)
            .background(Color.White)
            .height(IntrinsicSize.Max)
    ) {
        TableTextCell(
            text = i,
            modifier = Modifier.width(30.dp).padding(2.dp),
            isHeader = isHeader,
        )
        TableDivider()
        TableTextCell(
            text = name,
            modifier = Modifier.weight(1f).padding(2.dp),
            isHeader = isHeader,
        )
        TableDivider()
        TableTextCell(
            text = size,
            modifier = Modifier.width(80.dp).padding(2.dp),
            isHeader = isHeader,
            textAlign = TextAlign.Right,
        )
        TableDivider()
        TableTextCell(
            text = date,
            modifier = Modifier.width(200.dp).padding(2.dp),
            isHeader = isHeader,
            textAlign = TextAlign.Right,
        )
    }
}

@Preview
@Composable
fun PreviewFilesPanel() {
    Box(Modifier.background(Color.Gray)) {
        FilesPanel(
            FilesState.IDLE,
            mutableStateListOf(
                FileEntry(
                    name = "test_file.txt",
                    size = 143,
                    creationDate = "24 Jul 2039 14:46:18"
                ),
                FileEntry(name = "App crash.txt", size = 512),
                FileEntry(name = "anr.gz", size = 123456789L),
                FileEntry(name = "some screenshot.png", size = 456643),
            ),
            mutableStateOf<PreviewState?>(null),
            {},
            {},
            {},
        )
    }
}
