package com.alekso.dltstudio.plugins.virtualdevice

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alekso.dltmessage.SampleData
import com.alekso.dltstudio.model.contract.LogMessage
import com.alekso.dltstudio.plugins.virtualdevice.model.VirtualDevice
import com.alekso.dltstudio.uicomponents.CustomButton
import com.alekso.dltstudio.uicomponents.CustomDropDown

@Composable
fun DevicePreviewView(
    modifier: Modifier = Modifier,
    virtualDevices: SnapshotStateList<VirtualDevice>,
    logMessage: LogMessage?,
    onShowVirtualDeviceClicked: () -> Unit = {},
    onDeviceSelected: (Int) -> Unit,
    currentDeviceIndex: Int,
) {
    var deviceViews: List<DeviceView>? = null
    val paddingModifier = Modifier.padding(start = 4.dp, end = 4.dp)

    Column(modifier) {
        logMessage?.dltMessage?.let { message ->
            Header(
                modifier = paddingModifier, text = "Device Preview for #${logMessage.num}:"
            )
            deviceViews = DeviceView.parse(message.payloadText())
            if (deviceViews.isNullOrEmpty()) {
                Text(
                    modifier = Modifier.padding(start = 2.dp, end = 2.dp),
                    text = "No preview found",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                )
            }
        }

        CustomButton(
            modifier = Modifier.padding(0.dp),
            onClick = { onShowVirtualDeviceClicked() },
        ) {
            Text("Manage devices")
        }

        if (virtualDevices.isNotEmpty() && currentDeviceIndex < virtualDevices.size) {
            val currentDevice = virtualDevices[currentDeviceIndex]
            CustomDropDown(
                modifier = Modifier.width(200.dp).padding(horizontal = 4.dp),
                items = virtualDevices.map { device -> "${device.name}: ${device.width}x${device.height}" }.toMutableStateList(),
                initialSelectedIndex = currentDeviceIndex,
                onItemsSelected = onDeviceSelected
            )

            VirtualDevicePreview(
                modifier = Modifier.fillMaxWidth(),
                deviceSize = Size(
                    currentDevice.width.toFloat(),
                    currentDevice.height.toFloat()
                ),
                deviceViews = deviceViews,
            )
        } else {
            Text(
                "No virtual device found, please create one",
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}

// TODO: Copy - to remove
@Composable
fun Header(modifier: Modifier = Modifier, text: String) {
    Text(
        modifier = modifier.padding(top = 4.dp),
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight(600),
        fontSize = 11.sp,
        text = text
    )
    Divider()
}

@Preview
@Composable
fun PreviewEmptyDevicePreview() {
    DevicePreviewView(
        modifier = Modifier.fillMaxSize(),
        virtualDevices = mutableStateListOf(VirtualDevice(0, "Test", 3300, 900)),
        logMessage = null,
        currentDeviceIndex = 0,
        onDeviceSelected = {},
    )
}

@Preview
@Composable
fun PreviewDevicePreview() {
    val dltMessage = SampleData.create(
        payloadText = "TestView[2797]: onGlobalFocusChanged: oldFocus:com.ui.custom.ProgressBarFrameLayout{f5e8f76 VFE...CL. ......ID 2298,22-2835,709 #7f090453 app:id/theme_container aid=1073741849}, newFocus:com.android.car.ui.FocusParkingView{736743f VFED..... .F...... 0,0-1,1 #7f090194 app:id/focus_parking_view aid=1073741832} {bounds:Rect(0, 0 - 342, 240),hasBoundsTransaction,}"
    )
    DevicePreviewView(
        modifier = Modifier.fillMaxSize(),
        virtualDevices = mutableStateListOf(VirtualDevice(0, "Test", 3300, 900)),
        logMessage = LogMessage(dltMessage),
        currentDeviceIndex = 0,
        onDeviceSelected = {},
    )
}