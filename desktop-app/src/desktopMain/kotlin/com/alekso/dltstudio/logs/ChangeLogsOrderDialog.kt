package com.alekso.dltstudio.logs

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.rememberDialogState
import com.alekso.dltstudio.theme.SystemTheme
import com.alekso.dltstudio.theme.ThemeManager
import com.alekso.dltstudio.uicomponents.CustomButton
import com.alekso.dltstudio.uicomponents.CustomDropDown
import com.alekso.dltstudio.uicomponents.dialogs.DesktopDialogWindow

data class ChangeLogsOrderDialogState(
    val visible: Boolean,
) {
    companion object {
        val Default = ChangeLogsOrderDialogState(visible = false)
    }
}

@Composable
fun ChangeLogsOrderDialog(
    state: ChangeLogsOrderDialogState,
    onDialogClosed: () -> Unit,
    logsOrder: LogsOrder,
    onLogsOrderChanged: (newOrder: LogsOrder) -> Unit,
) {
    DesktopDialogWindow(
        visible = state.visible,
        onCloseRequest = onDialogClosed,
        title = "Change logs order",
        state = rememberDialogState(width = 400.dp, height = 400.dp)
    ) {
        ChangeLogsOrderDialogPanel(
            logsOrder = logsOrder,
            onApplyClicked = onLogsOrderChanged
        )
    }
}

@Composable
fun ChangeLogsOrderDialogPanel(onApplyClicked: (LogsOrder) -> Unit, logsOrder: LogsOrder) {
    var selectedMethod by remember { mutableStateOf(logsOrder) }

    Column(Modifier.padding(4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("Change logs order")
        CustomDropDown(
            modifier = Modifier,
            items = LogsOrder.entries.map { it.name }.toMutableStateList(),
            initialSelectedIndex = LogsOrder.entries.indexOf(logsOrder),
            onItemsSelected = { i ->
                selectedMethod = LogsOrder.entries[i]
            }
        )
        CustomButton(onClick = {
            onApplyClicked(selectedMethod)
        }) {
            Text("Apply")
        }
    }
}

@Preview
@Composable
fun PreviewChangeLogsOrderDialogPanel() {
    Column {
        ThemeManager.CustomTheme(SystemTheme(true)) {
            ChangeLogsOrderDialogPanel(onApplyClicked = {}, logsOrder = LogsOrder.Timestamp)
        }
        ThemeManager.CustomTheme(SystemTheme(false)) {
            ChangeLogsOrderDialogPanel(onApplyClicked = {}, logsOrder = LogsOrder.Timestamp)
        }
    }
}