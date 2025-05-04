package com.alekso.dltstudio.uicomponents

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TabsPanel(
    tabIndex: Int,
    tabs: SnapshotStateList<String>,
    callback: (Int) -> Unit,
    vertical: Boolean = false,
) {
    if (vertical) {
        Column {
            tabs.onEachIndexed { index, title ->
                Tab(title, index, tabIndex == index, callback)
            }
        }
    } else {
        Column {
            FlowRow(modifier = Modifier.wrapContentSize()) {
                tabs.forEachIndexed { i, tab ->
                    Tab(tab, i, tabIndex == i, callback)
                }
            }
            HorizontalDivider(Modifier.height(1.dp).fillMaxWidth())
        }
    }
}

@Composable
private fun Tab(title: String, index: Int, selected: Boolean, callback: (Int) -> Unit) {
    Box(
        modifier = Modifier.background(if (selected) Color.LightGray else Color.Transparent)
            .clickable(enabled = true, onClick = { callback(index) })
    ) {
        Text(
            text = title,
            maxLines = 1,
            modifier = Modifier.padding(start = 4.dp, top = 2.dp, end = 4.dp, bottom = 2.dp)
        )
    }
}

@Preview
@Composable
fun PreviewHorizontalTabs() {
    Column {
        TabsPanel(
            1,
            mutableStateListOf(
                "Logs",
                "CPU Usage",
                "Memory Analyze",
                "Device Explorer",
                "Insights"
            ),
            { i -> })
        Text("Content")
    }
}

@Preview
@Composable
fun PreviewVerticalTabs() {
    TabsPanel(1, mutableStateListOf("Logs", "CPU", "Memory"), { i -> }, vertical = true)
}
