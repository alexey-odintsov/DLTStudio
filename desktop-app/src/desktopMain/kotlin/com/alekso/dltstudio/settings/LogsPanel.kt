package com.alekso.dltstudio.com.alekso.dltstudio.settings

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alekso.dltmessage.PayloadStorageType
import com.alekso.dltstudio.model.SettingsLogs
import com.alekso.dltstudio.uicomponents.CustomButton
import com.alekso.dltstudio.uicomponents.CustomDropDown

@Composable
fun LogsPanel(callbacks: SettingsDialogCallbacks, settingsLogs: SettingsLogs) {
    println("Recompose LogsPanel $settingsLogs")
    var backendType by remember { mutableStateOf(settingsLogs.backendType) }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "Logs",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Payload storage type:", Modifier.width(150.dp))
            CustomDropDown(
                modifier = Modifier.width(200.dp).padding(horizontal = 4.dp),
                items = PayloadStorageType.entries.map { it.name },
                initialSelectedIndex = SettingsLogs.getIdByBackend(backendType),
                onItemsSelected = { index ->
                    backendType = SettingsLogs.getBackendById(index)
                }
            )
        }
        Text(
            "Storage type defines how app stores DLT messages payload in memory.",
            fontSize = 12.sp,
        )
        Text(
            "Binary – payload is stored in bytes, compact size, fast files loading but requires more time to search/analysis",
            fontSize = 12.sp,
        )
        Text(
            "Plain – payload is stored as Text, compact size, fast search/analysis but slow files loading.",
            fontSize = 12.sp,
        )
        Text(
            "Structured – payload is stored as object, normal files loading and normal time for search/analysis.",
            fontSize = 12.sp,
        )
        CustomButton(onClick = {
            callbacks.onSettingsLogsUpdate(SettingsLogs(backendType))
        }) {
            Text("Apply")
        }
    }
}

@Preview
@Composable
fun PreviewLogsPanel() {
    LogsPanel(
        callbacks = SettingsDialogCallbacks.Stub,
        settingsLogs = SettingsLogs.Default
    )
}