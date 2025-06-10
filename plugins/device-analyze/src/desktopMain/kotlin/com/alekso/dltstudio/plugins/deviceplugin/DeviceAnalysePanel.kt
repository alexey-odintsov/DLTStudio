package com.alekso.dltstudio.plugins.deviceplugin

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.alekso.dltstudio.theme.SystemTheme
import com.alekso.dltstudio.theme.ThemeManager
import com.alekso.dltstudio.uicomponents.CustomDropDown

@Composable
fun DeviceAnalysePanel(
    modifier: Modifier,
    responseState: SnapshotStateList<String>,
    onExecuteButtonClicked: (String) -> Unit,
) {
    val textStyle = Modifier.padding(horizontal = 4.dp).wrapContentHeight(Alignment.Top)
    var cmd by rememberSaveable { mutableStateOf("adb devices") }
    val scroll = rememberScrollState(0)
    val predefinedCommands = mutableStateListOf(
            "adb devices",
            "adb shell dumpsys -l",
            "adb shell dumpsys cpuinfo",
            "adb shell dumpsys meminfo",
            "adb shell dumpsys gpu",
            "adb shell dumpsys hardware_properties",
            "adb shell dumpsys runtime",
            "adb shell dumpsys user",
            "adb shell dumpsys window",
            "adb shell dumpsys meminfo package_name|pid [-d]",
            "adb shell dumpsys procstats --hours 1",
            "adb shell dumpsys gfxinfo package-name",
            "adb shell dumpsys gfxinfo package-name framestats",
    )

    Column(modifier = modifier) {
        Column {
            Text(modifier = textStyle, text = "Command:")
            Row {
                CustomDropDown(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    items = predefinedCommands,
                    initialSelectedIndex = 0,
                    onItemsSelected = { index -> cmd = predefinedCommands[index] }
                )
            }
            Row {
                OutlinedTextField(
                    modifier = Modifier.padding(4.dp).weight(1f)
                        .background(MaterialTheme.colorScheme.background),
                    value = cmd,
                    onValueChange = {
                        cmd = it
                    },
                    minLines = 3,
                    maxLines = 3,

                )
                Button(
                    modifier = Modifier.padding(4.dp),
                    onClick = {
                        onExecuteButtonClicked(cmd)
                    }) {
                    Text(modifier = Modifier.padding(0.dp), text = "Execute")
                }
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            SelectionContainer {
                OutlinedTextField(
                    readOnly = true,
                    modifier = Modifier.padding(horizontal = 4.dp).fillMaxSize(),
                    value = responseState.joinToString("\n"),
                    onValueChange = {},
                    textStyle = TextStyle.Default.copy(fontFamily = FontFamily.Monospace)
                )
//                Text(
//                    fontFamily = FontFamily.Monospace,
//                    fontSize = 10.sp,
//                    modifier = Modifier.padding(4.dp).fillMaxSize()
//                        .border(1.dp, MaterialTheme.colorScheme.tertiary,  RoundedCornerShape(2.0.dp))
//                        .background(MaterialTheme.colorScheme.surface)
//                        .height(100.dp).wrapContentHeight(Alignment.Top)
//                        .verticalScroll(scroll)
//                        .padding(horizontal = 4.dp), text = responseState.joinToString("\n")
//                )
            }
        }
    }
}


@Preview
@Composable
fun PreviewDeviceAnalysePanel() {
    Column {
        ThemeManager.CustomTheme(SystemTheme(false)) {
            Surface(

            ) {
                Text("Text")
            }
        }
        Box(Modifier.weight(1f)) {
            ThemeManager.CustomTheme(SystemTheme(true)) {
                DeviceAnalysePanel(Modifier, remember { mutableStateListOf("Response") }, {})
            }
        }
        Box(Modifier.weight(1f)) {
            ThemeManager.CustomTheme(SystemTheme(false)) {
                DeviceAnalysePanel(Modifier, remember { mutableStateListOf("Response") }, {})
            }
        }
    }
}