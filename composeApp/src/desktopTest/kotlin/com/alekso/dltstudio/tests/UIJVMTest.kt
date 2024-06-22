package com.alekso.dltstudio.tests

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.alekso.dltstudio.ui.TabsPanel
import dtlstudio.composeapp.generated.resources.Res
import dtlstudio.composeapp.generated.resources.tab_logs
import dtlstudio.composeapp.generated.resources.tab_timeline
import org.jetbrains.compose.resources.stringResource
import org.junit.Rule
import org.junit.Test

class UIJVMTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun `test tabs presence`() {
        rule.setContent {
            TabsPanel(
                0,
                listOf(stringResource(Res.string.tab_logs), stringResource(Res.string.tab_timeline))
            ) { }
        }

        rule.onNodeWithText("Logs").assertExists("Logs tab doesn't exist")
        rule.onNodeWithText("Timeline").assertExists("Timeline tab doesn't exist")
    }
}