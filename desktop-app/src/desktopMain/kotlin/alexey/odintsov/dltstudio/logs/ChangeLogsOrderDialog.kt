package alexey.odintsov.dltstudio.logs

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.rememberDialogState
import alexey.odintsov.dltstudio.theme.SystemTheme
import alexey.odintsov.dltstudio.theme.ThemeManager
import alexey.odintsov.dltstudio.uicomponents.CustomButton
import alexey.odintsov.dltstudio.uicomponents.CustomDropDown
import alexey.odintsov.dltstudio.uicomponents.dialogs.DesktopDialogWindow
import androidx.compose.ui.tooling.preview.Preview

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
        state = rememberDialogState(width = 300.dp, height = 140.dp)
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
        }, Modifier.align(Alignment.CenterHorizontally)) {
            Text("Apply")
        }
    }
}

@Preview
@Composable
private fun PreviewChangeLogsOrderDialogPanel() {
    Column {
        ThemeManager.CustomTheme(SystemTheme(true)) {
            ChangeLogsOrderDialogPanel(onApplyClicked = {}, logsOrder = LogsOrder.Timestamp)
        }
        ThemeManager.CustomTheme(SystemTheme(false)) {
            ChangeLogsOrderDialogPanel(onApplyClicked = {}, logsOrder = LogsOrder.Timestamp)
        }
    }
}