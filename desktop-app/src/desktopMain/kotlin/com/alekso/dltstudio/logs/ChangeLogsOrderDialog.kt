package com.alekso.dltstudio.logs

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.rememberDialogState
import com.alekso.dltstudio.plugins.contract.MessagesRepository
import com.alekso.dltstudio.uicomponents.CustomButton
import com.alekso.dltstudio.uicomponents.CustomDropDown
import com.alekso.dltstudio.uicomponents.dialogs.DesktopDialogWindow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

data class ChangeLogsOrderDialogState(
    val visible: Boolean,
) {
    companion object {
        val Default = ChangeLogsOrderDialogState(visible = false)
    }
}

@Composable
fun ChangeLogsOrderDialog(
    messagesRepository: MessagesRepository,
    state: ChangeLogsOrderDialogState,
    onDialogClosed: () -> Unit,
) {
    DesktopDialogWindow(
        visible = state.visible,
        onCloseRequest = onDialogClosed,
        title = "Change logs order",
        state = rememberDialogState(width = 400.dp, height = 400.dp)
    ) {
        ChangeLogsOrderDialogPanel(
            onApplyClicked = { method ->
                CoroutineScope(IO).launch {
                    when (method) {
                        1 -> messagesRepository.storeMessages(
                            messagesRepository.getMessages()
                                .sortedBy { it.dltMessage.timeStampUs }) // todo: then by ECU time/Count

                        else -> messagesRepository.storeMessages(
                            messagesRepository.getMessages()
                                .sortedBy { it.dltMessage.standardHeader.timeStamp }) // todo: then by Timestamp/Count
                    }
                }
            }
        )
    }
}

@Composable
fun ChangeLogsOrderDialogPanel(onApplyClicked: (Int) -> Unit) {
    var selectedMethod by rememberSaveable { mutableStateOf(0) }

    Column {
        Text("Change logs order")
        CustomDropDown(
            modifier = Modifier,
            items = mutableStateListOf("Timestamp", "ECU Time", "Mixed"),
            initialSelectedIndex = 0,
            onItemsSelected = { i ->
                selectedMethod = i
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
    ChangeLogsOrderDialogPanel(onApplyClicked = {})
}