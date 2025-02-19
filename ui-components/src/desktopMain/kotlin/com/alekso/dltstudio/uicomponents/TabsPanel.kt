package com.alekso.dltstudio.uicomponents

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


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
        Row {
            tabs.onEachIndexed { index, title ->
                Tab(title, index, tabIndex == index, callback)
            }
        }
        Divider()
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
            modifier = Modifier.padding(start = 4.dp, top = 2.dp, end = 4.dp, bottom = 2.dp)
        )
    }
}

@Preview
@Composable
fun PreviewHorizontalTabs() {
    TabsPanel(1, mutableStateListOf("Logs", "CPU", "Memory"), { i -> })
}

@Preview
@Composable
fun PreviewVerticalTabs() {
    TabsPanel(1, mutableStateListOf("Logs", "CPU", "Memory"), { i -> }, vertical = true)
}
