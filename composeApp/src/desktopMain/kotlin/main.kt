import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.alekso.dltparser.DLTParser
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
            val parseSessionViewModel = remember { ParseSessionViewModel(DLTParser) }
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

            MainWindow(parseSessionViewModel)
//        }
    }
}

@Preview
@Composable
fun AppDesktopPreview() {
    MainWindow(ParseSessionViewModel(DLTParser))
}