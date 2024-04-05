import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.alekso.dltparser.DLTParserV2
import com.alekso.dltstudio.MainViewModel
import com.alekso.dltstudio.timeline.TimelineViewModel
import com.alekso.dltstudio.ui.MainWindow
import org.junit.Rule
import org.junit.Test

class UIJVMTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun `test tabs presence`() {
        rule.setContent {
            var progress by remember { mutableStateOf(0f) }
            val onProgressUpdate: (Float) -> Unit = { i -> progress = i }

            val mainViewModel = remember { MainViewModel(DLTParserV2(), onProgressUpdate) }
            val timelineViewModel = remember { TimelineViewModel(onProgressUpdate) }

            MainWindow(mainViewModel, timelineViewModel, progress, onProgressUpdate)
        }

        rule.onNodeWithText("Logs").performClick()
        rule.onNodeWithContentDescription("Search").assertExists("Search button was not found",)
        rule.onNodeWithText("Timeline").performClick()
        rule.onNodeWithContentDescription("Analyze timeline").assertExists("Analyze button was not found",)
        rule.onNodeWithContentDescription("Timeline filters").assertExists("Timeline filters button was not found",)
    }
}