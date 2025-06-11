package com.alekso.dltstudio.settings

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alekso.dltstudio.model.PluginState
import com.alekso.dltstudio.model.SettingsPlugins
import com.alekso.dltstudio.plugins.DependencyManager
import com.alekso.dltstudio.plugins.contract.DLTStudioPlugin
import com.alekso.dltstudio.theme.SystemTheme
import com.alekso.dltstudio.theme.ThemeManager
import com.alekso.dltstudio.uicomponents.table.TableDivider
import com.alekso.dltstudio.uicomponents.table.TableTextCell
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.SplitPaneState
import org.jetbrains.compose.splitpane.VerticalSplitPane
import java.awt.Cursor

@OptIn(ExperimentalSplitPaneApi::class)
@Composable
fun PluginsPanel(
    settingsPlugins: SettingsPlugins,
    callbacks: SettingsPluginsCallbacks,
    vSplitterState: SplitPaneState
) {
    Column {
        val pluginManager = remember { DependencyManager.providePluginsManager() }
        val plugins = DependencyManager.providePluginsManager().plugins
        val paddingModifier = remember { Modifier.padding(horizontal = 4.dp) }

        Text(
            text = "Plugins",
            fontWeight = FontWeight.Bold,
            modifier = paddingModifier.padding(bottom = 10.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = paddingModifier.padding(bottom = 10.dp)
        ) {
            Text("Plugins path:")
            Text(
                modifier = Modifier.padding(start = 4.dp).background(Color.White),
                text = pluginManager.pluginsPath,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )
        }
        VerticalSplitPane(splitPaneState = vSplitterState) {
            first(50.dp) {
                Text("Registered plugins:")
                val listState = rememberLazyListState()
                LazyColumn(
                    Modifier.fillMaxSize(), state = listState
                ) {
                    stickyHeader {
                        PluginItem(
                            index = "#",
                            name = "Name",
                            version = "Version",
                            isHeader = true,
                            state = "State"
                        )
                    }
                    itemsIndexed(
                        items = plugins,
                        key = { _, key -> key },
                        contentType = { _, _ -> DLTStudioPlugin::class }) { i, plugin ->
                        val isEnabled = settingsPlugins.isEnabled(plugin.pluginClassName())
                        PluginItem(
                            index = i.toString(),
                            name = plugin.pluginName(),
                            version = plugin.pluginVersion(),
                            state = if (isEnabled) "enabled" else "disabled",
                            isRowSelected = settingsPlugins.selectedPlugin == plugin.pluginClassName(),
                            isEnabled = isEnabled,
                            onClick = { callbacks.onPluginClicked(plugin) },
                            onPluginStateChanged = { enabled ->
                                callbacks.onUpdatePluginState(
                                    PluginState(plugin.pluginClassName(), enabled)
                                )
                            },
                        )
                    }
                }
            }
            second(20.dp) {
                PluginInfoPanel(settingsPlugins, plugins)
            }
            splitter {
                visiblePart {
                    Box(
                        Modifier.height(1.dp).fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background)
                    )
                }
                handle {
                    Box(
                        Modifier.markAsHandle()
                            .pointerHoverIcon(PointerIcon(Cursor(Cursor.S_RESIZE_CURSOR)))
                            .background(SolidColor(Color.Gray), alpha = 0.50f).height(2.dp)
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun PluginInfoPanel(settingsPlugins: SettingsPlugins, plugins: MutableList<DLTStudioPlugin>) {
    val plugin = plugins.find { it.pluginClassName() == settingsPlugins.selectedPlugin }
    val scrollState = rememberScrollState()

    if (plugin != null) {
        Box {
            Column(
                Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(4.dp)
                    .verticalScroll(scrollState)
            ) {
                Text(plugin.pluginName(), fontWeight = FontWeight.Bold)
                Text("Version: ${plugin.pluginVersion()}")
                Text("Author: ${plugin.author()}")
                if (plugin.pluginLink() != null) {
                    Text("Link: ${plugin.pluginLink()}")
                }
                Text(text = plugin.description(), modifier = Modifier.padding(top = 4.dp))
            }
            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                adapter = rememberScrollbarAdapter(
                    scrollState = scrollState
                )
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
    state: String,
    isHeader: Boolean = false,
    isEnabled: Boolean = false,
    isRowSelected: Boolean = false,
    onClick: (() -> Unit)? = null,
    onPluginStateChanged: ((Boolean) -> Unit)? = null
) {
    Row(
        modifier.background(MaterialTheme.colorScheme.secondary).padding(bottom = 1.dp)
            .background(if (isRowSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.background)
            .height(IntrinsicSize.Max).clickable(onClick = { onClick?.invoke() })
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
        /**
        TableDivider()
        TableTextCell(
        text = state,
        modifier = Modifier.width(60.dp).padding(2.dp),
        isHeader = isHeader,
        )
        TableDivider()
        Box(modifier = Modifier.width(100.dp).padding(horizontal = 2.dp)) {
        if (!isHeader) {
        CustomButton(
        onClick = { onPluginStateChanged?.invoke(!isEnabled) },
        ) {
        Text(text = if (isEnabled) "Disable" else "Enable")
        }
        }
        }
         */
    }
}

@OptIn(ExperimentalSplitPaneApi::class)
@Preview
@Composable
fun PreviewPluginsPanel() {
    val selectedPlugin = "VirtualDevicePlugin"
    val pluginsState = listOf(PluginState(selectedPlugin, true))

    Column(Modifier.fillMaxSize()) {
        Column(Modifier.weight(1f)) {
            ThemeManager.CustomTheme(SystemTheme(false)) {
                PluginsPanel(
                    SettingsPlugins(
                        selectedPlugin = selectedPlugin,
                        pluginsState = pluginsState,
                    ), SettingsPluginsCallbacks.Stub, SplitPaneState(0.2f, true)
                )
            }
        }
        Column(Modifier.weight(1f)) {
            ThemeManager.CustomTheme(SystemTheme(true)) {
                PluginsPanel(
                    SettingsPlugins(
                        selectedPlugin = selectedPlugin,
                        pluginsState = pluginsState,
                    ), SettingsPluginsCallbacks.Stub, SplitPaneState(0.2f, true)
                )
            }
        }
    }
}