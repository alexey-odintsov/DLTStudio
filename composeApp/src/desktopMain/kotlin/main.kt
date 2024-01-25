import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.alekso.dltstudio.ui.MainWindow

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "DTL Studio",
        state = WindowState(width = 1280.dp, height = 768.dp)
    ) {
        MainWindow()
    }
}

@Preview
@Composable
fun AppDesktopPreview() {
    MainWindow()
}