package com.alekso.dltstudio.plugins.filesviewer

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.onClick
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.alekso.dltstudio.theme.AppTheme
import com.alekso.dltstudio.theme.SystemTheme
import com.alekso.dltstudio.theme.ThemeManager
import com.alekso.dltstudio.uicomponents.CustomButton
import com.alekso.dltstudio.uicomponents.table.TableDivider
import com.alekso.dltstudio.uicomponents.table.TableTextCell
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.rememberSplitPaneState

@OptIn(ExperimentalSplitPaneApi::class)
@Composable
fun FilesPanel(
    analyzeState: FilesState,
    files: SnapshotStateList<FileEntry>,
    previewState: State<PreviewState?>,
    onSearchButtonClicked: () -> Unit,
    onFileEntryClicked: (FileEntry) -> Unit,
) {

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
            val splitterState = rememberSplitPaneState(0.65f)

            HorizontalSplitPane(
                splitPaneState = splitterState
            ) {
                first(20.dp) {
                    FilesList(files, onFileEntryClicked, listState, previewState.value)
                }
                second(20.dp) {
                    FilePreview(previewState.value)
                }
                splitter {
                    visiblePart {
                        Box(
                            Modifier
                                .width(1.dp)
                                .fillMaxHeight()
                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        )
                    }

                    handle {
                        Box(
                            Modifier
                                .markAsHandle()
                                .width(4.dp)
                                .fillMaxHeight()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FilePreview(
    previewState: PreviewState?,
) {
    when (val state = previewState) {
        is TextPreviewState -> {
            TextContent(state, state.saveCallback)
        }

        is ImagePreviewState -> {
            ImageFilePreview(state)
        }

        is FilePreviewState -> {
            OtherFilePreview(state)
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FilesList(
    files: SnapshotStateList<FileEntry>,
    onFileEntryClicked: (FileEntry) -> Unit,
    listState: LazyListState,
    preview: PreviewState?
) {
    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            Modifier.fillMaxSize(),
            state = listState
        ) {
            stickyHeader {
                Row(
                    Modifier
                        .padding(bottom = 1.dp)
                        .background(AppTheme.colors.logRow)
                        .height(IntrinsicSize.Max)
                ) {
                    TableTextCell(
                        text = "#",
                        modifier = Modifier.width(30.dp).padding(2.dp),
                    )
                    TableDivider()
                    TableTextCell(
                        text = "Name",
                        modifier = Modifier.weight(1f).padding(2.dp),
                    )
                    TableDivider()
                    TableTextCell(
                        text = "Size",
                        modifier = Modifier.width(80.dp).padding(2.dp),
                        textAlign = TextAlign.Right,
                    )
                    TableDivider()
                    TableTextCell(
                        text = "Date created",
                        modifier = Modifier.width(200.dp).padding(2.dp),
                        textAlign = TextAlign.Right,
                    )
                }
            }
            itemsIndexed(
                items = files,
                key = { _, key -> key.serialNumber },
                contentType = { _, _ -> FileEntry::class }) { i, fileEntry ->
                FileItem(
                    Modifier.onClick(
                        onClick = { onFileEntryClicked(fileEntry) },
                    ),
                    i = i.toString(),
                    fileEntry = fileEntry,
                    isSelected = preview?.entry == fileEntry,
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

@Composable
fun FileItem(
    modifier: Modifier = Modifier,
    i: String,
    fileEntry: FileEntry,
    isSelected: Boolean = false,
) {
    val sizeText = LocalFormatter.current.formatSizeHuman(fileEntry.size)
    val date = fileEntry.creationDate
    val isValid = fileEntry.isComplete()

    Row(
        modifier
            .padding(bottom = 1.dp)
            .background(if (isSelected) MaterialTheme.colorScheme.secondary else AppTheme.colors.logRow)
            .height(IntrinsicSize.Max)
    ) {
        TableTextCell(
            text = i,
            modifier = Modifier.width(30.dp).padding(2.dp),
        )
        TableDivider()
        TableTextCell(
            text = fileEntry.name,
            modifier = Modifier.weight(1f).padding(2.dp),
        )
        TableDivider()
        TableTextCell(
            text = if (isValid) sizeText else "$sizeText (${fileEntry.getContent()?.size})",
            modifier = Modifier.width(80.dp).padding(2.dp),
            textAlign = TextAlign.Right,
            textColor = if (isValid) Color.Unspecified else MaterialTheme.colorScheme.error
        )
        TableDivider()
        TableTextCell(
            text = date,
            modifier = Modifier.width(200.dp).padding(2.dp),
            textAlign = TextAlign.Right,
        )
    }
}

@Preview
@Composable
fun PreviewFilesPanel() {
    ThemeManager.CustomTheme(SystemTheme(true)) {
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
            )
        }
    }
}
