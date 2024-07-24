package com.alekso.dltstudio.logs.infopanel

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.rememberDialogState
import com.alekso.dltstudio.model.VirtualDevice
import com.alekso.dltstudio.ui.CustomButton
import com.alekso.dltstudio.ui.CustomEditText


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
    DialogWindow(
        visible = visible, onCloseRequest = onDialogClosed,
        title = if (device.id >= 0) "Edit virtual device" else "Add new virtual device",
        state = rememberDialogState(width = 700.dp, height = 500.dp)
    ) {
        EditVirtualDevicePanel(
            device = device,
            onDeviceUpdate = onItemUpdate,
            onDialogClosed,
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
) {
    var deviceName by rememberSaveable { mutableStateOf(device.name) }
    var deviceWidth by rememberSaveable { mutableStateOf(device.size.width.toInt()) }
    var deviceHeight by rememberSaveable { mutableStateOf(device.size.height.toInt()) }
    val colNameStyle = Modifier.width(COL_NAME_SIZE_DP).padding(horizontal = 4.dp)

    Column(
        Modifier.width(1000.dp).padding(4.dp),
    ) {
        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
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
                Text(modifier = colNameStyle, text = "width")
                CustomEditText(
                    modifier = Modifier.width(COL_VALUE),
                    value = deviceWidth.toString(),
                    onValueChange = {
                        deviceWidth = it.toInt()
                    }
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(modifier = colNameStyle, text = "height")
                CustomEditText(
                    modifier = Modifier.width(COL_VALUE),
                    value = deviceHeight.toString(),
                    onValueChange = {
                        deviceHeight = it.toInt()
                    }
                )
            }
        }

        CustomButton(onClick = {
            onDeviceUpdate(
                VirtualDevice(
                    id = device.id, // TODO: Increase index
                    name = deviceName,
                    size = Size(width = deviceWidth.toFloat(), height = deviceHeight.toFloat())
                )
            )
            onDialogClosed()
        }) {
            Text(text = if (device.id >= 0) "Update" else "Add")
        }
    }
}

@Preview
@Composable
fun PreviewEditTimelineFilterDialog() {
    Column(Modifier.background(Color(238, 238, 238))) {
        EditVirtualDevicePanel(
            device = VirtualDevice(1, "Test device", Size(500f, 500f)),
            onDeviceUpdate = { _ -> },
            onDialogClosed = {},
        )
    }
}
