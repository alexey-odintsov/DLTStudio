package com.alekso.dltstudio.tests

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.alekso.dltstudio.uicomponents.TabsPanel
import org.junit.Rule
import org.junit.Test

class UIJVMTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun `test tabs presence`() {
        rule.setContent {
            TabsPanel(0, mutableStateListOf("Logs", "Timeline"), { })
        }

        rule.onNodeWithText("Logs").assertExists("Logs tab doesn't exist")
        rule.onNodeWithText("Timeline").assertExists("Timeline tab doesn't exist")
    }
}