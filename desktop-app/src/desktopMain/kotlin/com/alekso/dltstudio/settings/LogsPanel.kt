package com.alekso.dltstudio.settings

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alekso.dltmessage.PayloadStorageType
import com.alekso.dltstudio.model.SettingsLogs
import com.alekso.dltstudio.theme.SystemTheme
import com.alekso.dltstudio.theme.ThemeManager
import com.alekso.dltstudio.uicomponents.CustomButton
import com.alekso.dltstudio.uicomponents.CustomDropDown


@Composable
private fun RowScope.selectedFileStyle() =
    Modifier.weight(1f)
        .heightIn(24.dp)
        .align(Alignment.CenterVertically)
        .background(MaterialTheme.colorScheme.background)
        .padding(4.dp)

@Composable
fun LogsPanel(callbacks: SettingsDialogCallbacks, settingsLogs: SettingsLogs) {
    var backendType = settingsLogs.backendType
    val defaultLogsFolderPath = settingsLogs.defaultLogsFolderPath
    val defaultColorFiltersFolderPath = settingsLogs.defaultColorFiltersFolderPath
    val paddingModifier = remember { Modifier.padding(horizontal = 4.dp) }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "Logs",
            fontWeight = FontWeight.Bold,
            modifier = paddingModifier.padding(bottom = 10.dp)
        )

        Row(verticalAlignment = Alignment.CenterVertically, modifier = paddingModifier) {
            Text("Payload storage type:", Modifier.width(150.dp))
            CustomDropDown(
                modifier = Modifier.width(200.dp).padding(horizontal = 4.dp),
                items = PayloadStorageType.entries.map { it.name }.toMutableStateList(),
                initialSelectedIndex = SettingsLogs.getIdByBackend(backendType),
                onItemsSelected = { index ->
                    backendType = SettingsLogs.getBackendById(index)
                }
            )
        }
        Text(
            modifier = paddingModifier,
            text = "Storage type defines how app stores DLT messages payload in memory.",
            fontSize = 12.sp,
        )
        Text(
            modifier = paddingModifier,
            text = "Binary – payload is stored in bytes, compact size, fast files loading but requires more time to search/analysis",
            fontSize = 12.sp,
        )
        Text(
            modifier = paddingModifier,
            text = "Plain – payload is stored as Text, compact size, fast search/analysis but slow files loading.",
            fontSize = 12.sp,
        )
        Text(
            modifier = paddingModifier,
            text = "Structured – payload is stored as object, normal files loading and normal time for search/analysis.",
            fontSize = 12.sp,
        )
        CustomButton(
            modifier = paddingModifier,
            onClick = {
                callbacks.onSettingsLogsUpdate(
                    SettingsLogs(
                        backendType,
                        defaultLogsFolderPath,
                        defaultColorFiltersFolderPath
                    )
                )
            }) {
            Text("Apply")
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = paddingModifier
        ) {
            Text("Default logs location:")
            Text(
                modifier = selectedFileStyle(),
                text = defaultLogsFolderPath ?: ""
            )
            CustomButton(
                onClick = { callbacks.onOpenDefaultLogsFolderClicked() }) {
                Text("Choose...")
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = paddingModifier
        ) {
            Text("Default color filters location:")
            Text(
                modifier = selectedFileStyle(),
                text = defaultColorFiltersFolderPath ?: ""
            )
            CustomButton(
                onClick = { callbacks.onOpenDefaultColorFiltersFolderClicked() }) {
                Text("Choose...")
            }
        }
    }
}

@Preview
@Composable
fun PreviewLogsPanelTheme() {
    Column {
        ThemeManager.CustomTheme(SystemTheme(true)) {
            PreviewLogsPanel()
        }
        ThemeManager.CustomTheme(SystemTheme(false)) {
            PreviewLogsPanel()
        }
    }
}

@Preview
@Composable
fun PreviewLogsPanel() {
    LogsPanel(
        callbacks = SettingsDialogCallbacks.Stub,
        settingsLogs = SettingsLogs.Default.copy(defaultLogsFolderPath = "file/path/goes/here"),
    )
}