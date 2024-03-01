import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.alekso.dltparser.DLTParserV2
import com.alekso.dltstudio.MainViewModel
import com.alekso.dltstudio.ui.FileChooserDialog
import com.alekso.dltstudio.ui.FileChooserDialogState
import com.alekso.dltstudio.ui.MainWindow

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "DTL Studio",
        state = WindowState(width = 1280.dp, height = 768.dp)
    ) {
//        MaterialTheme(
//            colors = MaterialTheme.colors,
//            typography = MaterialTheme.typography, // todo: Default font size is too big
//            shapes = MaterialTheme.shapes
//        ) {
        var progress by remember { mutableStateOf(0f) }
        val onProgressUpdate: (Float) -> Unit = { i -> progress = i }


        val mainViewModel = remember { MainViewModel(DLTParserV2(), onProgressUpdate) }
        var stateIOpenFileDialog by remember { mutableStateOf(FileChooserDialogState()) }

        MenuBar {
            Menu("File") {
                Item(
                    "Open",
                    onClick = {
                        stateIOpenFileDialog = FileChooserDialogState(
                            true,
                            FileChooserDialogState.DialogContext.OPEN_DLT_FILE
                        )
                    })
            }
            Menu("Filters") {
                Item(
                    "Open",
                    onClick = {
                        stateIOpenFileDialog = FileChooserDialogState(
                            true,
                            FileChooserDialogState.DialogContext.OPEN_FILTER_FILE
                        )
                    })
                Item(
                    "Save",
                    onClick = {
                        stateIOpenFileDialog = FileChooserDialogState(
                            true,
                            FileChooserDialogState.DialogContext.SAVE_FILTER_FILE
                        )
                    })
            }
        }

        if (stateIOpenFileDialog.visibility) {
            FileChooserDialog(
                dialogContext = stateIOpenFileDialog.dialogContext,
                title = when (stateIOpenFileDialog.dialogContext) {
                    FileChooserDialogState.DialogContext.OPEN_DLT_FILE -> "Open DLT file"
                    FileChooserDialogState.DialogContext.OPEN_FILTER_FILE -> "Open filters"
                    FileChooserDialogState.DialogContext.UNKNOWN -> "Open file"
                    FileChooserDialogState.DialogContext.SAVE_FILTER_FILE -> "Save filter"
                },
                onFileSelected = { file ->
                    when (stateIOpenFileDialog.dialogContext) {
                        FileChooserDialogState.DialogContext.OPEN_DLT_FILE -> {
                            file?.let {
                                mainViewModel.parseFile(listOf(it))
                            }
                        }

                        FileChooserDialogState.DialogContext.OPEN_FILTER_FILE -> {
                            file?.let {
                                mainViewModel.loadColorFilters(it)
                            }
                        }

                        FileChooserDialogState.DialogContext.SAVE_FILTER_FILE -> {
                            file?.let {
                                mainViewModel.saveColorFilters(it)
                            }
                        }

                        FileChooserDialogState.DialogContext.UNKNOWN -> {

                        }
                    }
                    stateIOpenFileDialog = stateIOpenFileDialog.copy(visibility = false)
                },
            )
        }

        MainWindow(mainViewModel, progress, onProgressUpdate)
//        }
    }
}