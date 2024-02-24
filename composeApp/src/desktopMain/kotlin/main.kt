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
import com.alekso.dltstudio.ParseSessionViewModel
import com.alekso.dltstudio.ui.FileChooserDialog
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


        val parseSessionViewModel = remember { ParseSessionViewModel(DLTParserV2(), onProgressUpdate) }
            val stateIsOpenFileDialog = remember { mutableStateOf(false) }

            MenuBar {
                Menu("File") {
                    Item("Open", onClick = { stateIsOpenFileDialog.value = true })
                }
            }

            if (stateIsOpenFileDialog.value) {
                FileChooserDialog(
                    title = "Open file",
                    onFileSelected = { file ->
                        stateIsOpenFileDialog.value = false
                        file?.let {
                            parseSessionViewModel.parseFile(listOf(it))
                        }
                    },
                )
            }

            MainWindow(parseSessionViewModel, progress, onProgressUpdate)
//        }
    }
}