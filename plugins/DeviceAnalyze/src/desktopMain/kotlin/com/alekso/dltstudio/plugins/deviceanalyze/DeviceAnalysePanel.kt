package com.alekso.dltstudio.plugins.deviceanalyze

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alekso.dltstudio.uicomponents.CustomButton
import com.alekso.dltstudio.uicomponents.CustomDropDown
import com.alekso.dltstudio.uicomponents.CustomEditText

@Composable
fun DeviceAnalysePanel(
    modifier: Modifier,
    responseState: SnapshotStateList<String>,
    onExecuteButtonClicked: (String) -> Unit,
) {
    val textStyle = Modifier.padding(horizontal = 4.dp).wrapContentHeight(Alignment.Top)
    var cmd by rememberSaveable { mutableStateOf("adb devices") }
    val scroll = rememberScrollState(0)
    val predefinedCommands = remember {
        listOf(
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
    }

    Column(modifier = modifier) {
        Column {
            Text(modifier = textStyle, text = "Command:")
            Row {
                CustomDropDown(
                    modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
                    items = predefinedCommands,
                    initialSelectedIndex = 0,
                    onItemsSelected = { index -> cmd = predefinedCommands[index] }
                )
            }
            Row {
                CustomEditText(
                    modifier = Modifier.padding(vertical = 4.dp).weight(1f).height(60.dp),
                    singleLine = false,
                    value = cmd, onValueChange = {
                        cmd = it
                    }
                )
                CustomButton(
                    modifier = Modifier.padding(4.dp),
                    onClick = {
                        onExecuteButtonClicked(cmd)
                    }) {
                    Text(text = "Execute")
                }
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            SelectionContainer {
                Text(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(4.dp).fillMaxSize().background(Color.White)
                        .height(100.dp).wrapContentHeight(Alignment.Top)
                        .verticalScroll(scroll)
                        .padding(horizontal = 4.dp), text = responseState.joinToString("\n")
                )
            }
        }
    }
}