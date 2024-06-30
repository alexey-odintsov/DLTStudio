package com.alekso.dltstudio.logs.infopanel

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltparser.dlt.standardheader.HeaderType
import com.alekso.dltparser.dlt.standardheader.StandardHeader
import com.alekso.dltstudio.TimeFormatter
import com.alekso.dltstudio.model.LogMessage
import com.alekso.dltstudio.ui.CustomDropDown

@Composable
fun DevicePreviewView(
    modifier: Modifier = Modifier,
    logMessage: LogMessage?,
    messageIndex: Int,
) {
    if (logMessage == null) return

    val paddingModifier = Modifier.padding(start = 4.dp, end = 4.dp)

    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        logMessage.dltMessage.let {
            Header(
                modifier = paddingModifier, text = "Device Preview for #$messageIndex:"
            )
//            val headerText =
//                "${TimeFormatter.formatDateTime(it.timeStampNano)} " + "${it.extendedHeader?.applicationId} " + "${it.extendedHeader?.contextId} "
//            TableRow(0, "", headerText)
//            TableRow(0, "", it.payload)

            val deviceViews = DeviceView.parse(logMessage.dltMessage.payload)
            if (deviceViews.isNullOrEmpty()) {
                Text(
                    modifier = Modifier.padding(start = 2.dp, end = 2.dp),
                    text = "No preview found",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                )
            } else {
                val deviceList = listOf(
                    VirtualDevice(0, "Square", Size(1800f, 1800f)),
                    VirtualDevice(1, "Wide", Size(3000f, 1200f)),
                )
                var currentDevice by rememberSaveable { mutableStateOf(deviceList[0]) }
                CustomDropDown(modifier = Modifier.width(200.dp).padding(horizontal = 4.dp),
                    items = deviceList.map { device -> "${device.name}: ${device.size.width.toInt()}x${device.size.height.toInt()}" },
                    initialSelectedIndex = 0,
                    onItemsSelected = { i ->
                        currentDevice = deviceList[i]
                    })
                Row(modifier = Modifier.weight(1f).fillMaxWidth().background(Color.Gray)) {
                    VirtualDevicePreview(
                        modifier = Modifier.fillMaxSize(),
                        deviceSize = currentDevice.size,
                        deviceViews
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewDevicePreview() {
    val standardHeader = StandardHeader(
        headerType = HeaderType(
            64.toByte(),
            useExtendedHeader = false,
            payloadBigEndian = true,
            withEcuId = false,
            withSessionId = false,
            withTimestamp = false,
            versionNumber = 1,
        ), 1.toUByte(), 10.toUShort()
    )
    val payload =
        "TestView[2797]: onGlobalFocusChanged: oldFocus:com.ui.custom.ProgressBarFrameLayout{f5e8f76 VFE...CL. ......ID 2298,22-2835,709 #7f090453 app:id/theme_container aid=1073741849}, newFocus:com.android.car.ui.FocusParkingView{736743f VFED..... .F...... 0,0-1,1 #7f090194 app:id/focus_parking_view aid=1073741832} {bounds:Rect(0, 0 - 342, 240),hasBoundsTransaction,}"
    val dltMessage = DLTMessage(
        1L,
        "ECU",
        standardHeader = standardHeader,
        extendedHeader = null,
        payload = payload,
        sizeBytes = 100,
    )
    DevicePreviewView(
        modifier = Modifier.fillMaxSize(), logMessage = LogMessage(dltMessage), messageIndex = 0
    )
}