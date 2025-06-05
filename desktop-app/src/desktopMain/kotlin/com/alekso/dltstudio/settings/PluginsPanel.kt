package com.alekso.dltstudio.settings

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alekso.dltstudio.plugins.DependencyManager
import com.alekso.dltstudio.plugins.contract.DLTStudioPlugin
import com.alekso.dltstudio.uicomponents.table.TableDivider
import com.alekso.dltstudio.uicomponents.table.TableTextCell

@Composable
fun PluginsPanel(callbacks: SettingsDialogCallbacks) {
    val pluginManager = remember { DependencyManager.providePluginsManager() }
    val plugins = DependencyManager.providePluginsManager().plugins
    Text(
        text = "Plugins", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 10.dp)
    )
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("Plugins path:")
        Text(
            modifier = Modifier.padding(start = 4.dp).background(Color.White),
            text = pluginManager.pluginsPath,
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace
        )
    }

    Spacer(Modifier.height(6.dp))

    Text("Registered plugins:")
    val listState = rememberLazyListState()
    LazyColumn(
        Modifier.fillMaxSize(), state = listState
    ) {
        stickyHeader {
            PluginItem(
                index = "#", name = "Name", version = "Version", isHeader = true
            )
        }
        itemsIndexed(
            items = plugins,
            key = { _, key -> key },
            contentType = { _, _ -> DLTStudioPlugin::class }) { i, plugin ->
            PluginItem(
                index = i.toString(),
                name = plugin.pluginName(),
                version = plugin.pluginVersion(),
            )
        }
    }
}

@Composable
fun PluginItem(
    modifier: Modifier = Modifier,
    index: String,
    name: String,
    version: String,
    isHeader: Boolean = false,
) {
    Row(
        modifier.background(Color(0xFFEEEEEE)).padding(bottom = 1.dp).background(Color.White)
            .height(IntrinsicSize.Max)
    ) {
        TableTextCell(
            text = index,
            modifier = Modifier.width(30.dp).padding(2.dp),
            isHeader = isHeader,
        )
        TableDivider()
        TableTextCell(
            text = name,
            modifier = Modifier.weight(1f).padding(2.dp),
            isHeader = isHeader,
        )
        TableDivider()
        TableTextCell(
            text = version,
            modifier = Modifier.width(60.dp).padding(2.dp),
            isHeader = isHeader,
        )
    }
}

@Preview
@Composable
fun PreviewPluginsPanel() {
    Column {
        PluginsPanel(SettingsDialogCallbacks.Stub)
    }
}