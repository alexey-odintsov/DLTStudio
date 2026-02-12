package com.alekso.dltstudio.uicomponents

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.alekso.dltstudio.theme.SystemTheme
import com.alekso.dltstudio.theme.ThemeManager


@Composable
fun TabsPanel(
    tabIndex: Int,
    tabs: List<String>,
    callback: (Int) -> Unit,
    vertical: Boolean = false,
) {
    if (vertical) {
        Column {
            tabs.onEachIndexed { i, title ->
                Tab(title, i, tabIndex == i, callback)
            }
        }
    } else {
        Column {
            FlowRow(modifier = Modifier.wrapContentSize()) {
                tabs.forEachIndexed { i, title ->
                    Tab(title, i, tabIndex == i, callback)
                }
            }
            HorizontalDivider(Modifier.height(1.dp).fillMaxWidth())
        }
    }
}


@Composable
private fun Tab(title: String, index: Int, selected: Boolean, callback: (Int) -> Unit) {
    val backgroundModifier =
        if (selected) Modifier.background(MaterialTheme.colorScheme.secondary) else Modifier
    Box(
        modifier = Modifier.then(backgroundModifier)
            .clickable(enabled = true, onClick = { callback(index) })
    ) {
        Text(
            text = title,
            maxLines = 1,
            color = if (selected) MaterialTheme.colorScheme.onSecondary else Color.Unspecified,
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
            { _ -> })
        Text("Content")
    }
}

@Preview
@Composable
fun PreviewVerticalTabs() {
    Column {
        ThemeManager.CustomTheme(SystemTheme(true)) {
            TabsPanel(1, mutableStateListOf("Logs", "CPU", "Memory"), { _ -> }, vertical = false)
        }
        ThemeManager.CustomTheme(SystemTheme(false)) {
            TabsPanel(1, mutableStateListOf("Logs", "CPU", "Memory"), { _ -> }, vertical = false)
        }
    }
}
