package com.alekso.dltstudio.plugins.virtualdevice

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.rememberDialogState
import com.alekso.dltstudio.plugins.virtualdevice.model.VirtualDevice
import com.alekso.dltstudio.uicomponents.CustomButton
import com.alekso.dltstudio.uicomponents.CustomEditText


class EditVirtualDeviceDialogState(
    var visible: Boolean,
    var device: VirtualDevice = VirtualDevice.Empty,
)

@Composable
fun EditVirtualDeviceDialog(
    visible: Boolean,
    onDialogClosed: () -> Unit,
    device: VirtualDevice,
    onItemUpdate: (VirtualDevice) -> Unit,
) {
    var validationErrors = remember { mutableStateListOf<String>() }
    DialogWindow(
        visible = visible, onCloseRequest = onDialogClosed,
        title = if (device.id >= 0) "Edit virtual device" else "Add new virtual device",
        state = rememberDialogState(width = 400.dp)
    ) {
        EditVirtualDevicePanel(
            device = device,
            onDeviceUpdate = onItemUpdate,
            onDialogClosed = onDialogClosed,
            onValidationFailed = { errors ->
                validationErrors.clear()
                validationErrors.addAll(errors)
            },
            validationErrors = validationErrors
        )
    }
}

private val COL_NAME_SIZE_DP = 150.dp
private val COL_VALUE = 250.dp


@Composable
fun EditVirtualDevicePanel(
    device: VirtualDevice,
    onDeviceUpdate: (VirtualDevice) -> Unit,
    onDialogClosed: () -> Unit,
    onValidationFailed: (List<String>) -> Unit = {},
    validationErrors: List<String> = emptyList()
) {
    var deviceName by remember { mutableStateOf(device.name) }
    var deviceWidth by remember { mutableStateOf(device.width.toString()) }
    var deviceHeight by remember { mutableStateOf(device.height.toString()) }
    val colNameStyle = Modifier.width(COL_NAME_SIZE_DP).padding(horizontal = 4.dp)

    Column(
        Modifier.width(1000.dp).padding(4.dp),
    ) {
        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(modifier = colNameStyle, text = "Name")
                CustomEditText(
                    modifier = Modifier.width(COL_VALUE),
                    value = deviceName, onValueChange = {
                        deviceName = it
                    }
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(modifier = colNameStyle, text = "Width")
                CustomEditText(
                    modifier = Modifier.width(COL_VALUE),
                    value = deviceWidth,
                    onValueChange = {
                        deviceWidth = it
                    }
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(modifier = colNameStyle, text = "Height")
                CustomEditText(
                    modifier = Modifier.width(COL_VALUE),
                    value = deviceHeight,
                    onValueChange = {
                        deviceHeight = it
                    },
                )
            }

            validationErrors.forEach {
                Text(text = it, color = Color.Red)
            }
        }

        CustomButton(onClick = {
            val newValidationErrors = validateInput(deviceName, deviceWidth, deviceHeight)
            if (newValidationErrors.isEmpty()) {
                onDeviceUpdate(
                    VirtualDevice(
                        id = device.id,
                        name = deviceName,
                        width = deviceWidth.toInt(),
                        height = deviceHeight.toInt(),
                    )
                )
                onDialogClosed()
            } else {
                onValidationFailed(newValidationErrors)
            }
        }) {
            Text(text = if (device.id >= 0) "Update" else "Add")
        }
    }
}


fun isValidSizeString(value: String): Boolean {
    val intValue = value.toIntOrNull()
    return intValue != null && intValue > 0
}

fun validateInput(name: String, width: String, height: String): List<String> {
    val errors = mutableListOf<String>()
    if (name.isEmpty()) {
        errors.add("Name is empty!")
    }
    if (!isValidSizeString(width)) {
        errors.add("Width is less than 0!")
    }
    if (!isValidSizeString(height)) {
        errors.add("Height is less than 0!")
    }
    return errors
}

@Preview
@Composable
fun PreviewEditTimelineFilterDialog() {
    Column(Modifier.background(Color(238, 238, 238))) {
        EditVirtualDevicePanel(
            device = VirtualDevice(1, "Test device", 500, 500),
            onDeviceUpdate = { _ -> },
            onDialogClosed = {},
            validationErrors = listOf("Width is null")
        )
    }
}
