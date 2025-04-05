package com.alekso.dltstudio.logs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import com.alekso.dltstudio.logs.colorfilters.ColorFiltersDialog
import com.alekso.dltstudio.logs.infopanel.VirtualDevicesDialog
import com.alekso.dltstudio.model.contract.LogMessage
import com.alekso.dltstudio.plugins.contract.PluginPanel
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi


class LogsPlugin(
    private val viewModel: LogsViewModel,
) : PluginPanel {
    override fun getPanelName(): String = "Logs"

    @OptIn(ExperimentalSplitPaneApi::class)
    @Composable
    override fun renderPanel(modifier: Modifier) {
        val clipboardManager = LocalClipboardManager.current

        if (viewModel.colorFiltersDialogState.value) {
            ColorFiltersDialog(
                visible = viewModel.colorFiltersDialogState.value,
                onDialogClosed = { viewModel.colorFiltersDialogState.value = false },
                colorFilters = viewModel.colorFilters,
                callbacks = viewModel.colorFiltersDialogCallbacks,
            )
        }
        if (viewModel.devicePreviewsDialogState.value) {
            VirtualDevicesDialog(
                visible = viewModel.devicePreviewsDialogState.value,
                onDialogClosed = { viewModel.devicePreviewsDialogState.value = false },
                virtualDevices = viewModel.virtualDevices,
                onVirtualDeviceUpdate = { device -> viewModel.onVirtualDeviceUpdate(device) },
                onVirtualDeviceDelete = { device -> viewModel.onVirtualDeviceDelete(device) },
            )
        }
        if (viewModel.removeLogsDialogState.value.visible) {
            RemoveLogsDialog(
                visible = viewModel.removeLogsDialogState.value.visible,
                message = viewModel.removeLogsDialogState.value.message,
                onDialogClosed = {
                    viewModel.removeLogsDialogState.value = RemoveLogsDialogState(false)
                },
                onFilterClicked = { f -> viewModel.removeMessagesByFilters(f) },
            )
        }
        val searchState by viewModel.searchState.collectAsState()

        LogsPanel(
            modifier = modifier,
            columnParams = viewModel.columnParams,
            logMessages = viewModel.logMessages,
            searchState = searchState,
            searchAutoComplete = viewModel.searchAutocomplete,
            logInsights = viewModel.logInsights,
            virtualDevices = viewModel.virtualDevices,
            searchResult = viewModel.searchResults,
            searchIndexes = viewModel.searchIndexes,
            colorFilters = viewModel.colorFilters,
            logsToolbarState = viewModel.logsToolbarState,
            logsToolbarCallbacks = viewModel.logsToolbarCallbacks,
            vSplitterState = viewModel.vSplitterState,
            hSplitterState = viewModel.hSplitterState,
            logsListState = viewModel.logsListState,
            logSelection = viewModel.logSelection,
            searchListState = viewModel.searchListState,
            onLogsRowSelected = { i, r ->
                viewModel.onLogsRowSelected(i, r)
            },
            onSearchRowSelected = { i, r ->
                viewModel.onSearchRowSelected(i, r)
            },
            onCommentUpdated = { logMessage, comment ->
                viewModel.updateComment(
                    logMessage, comment
                )
            },
            rowContextMenuCallbacks = object : RowContextMenuCallbacks {
                override fun onCopyClicked(text: AnnotatedString) {
                    clipboardManager.setText(text)
                }

                override fun onMarkClicked(i: Int, message: LogMessage) {
                    viewModel.markMessage(i, message)
                }

                override fun onRemoveClicked(
                    context: LogRemoveContext, filter: String
                ) {
                    viewModel.removeMessages(context, filter)
                }

                override fun onRemoveDialogClicked(message: LogMessage) {
                    viewModel.removeLogsDialogState.value = RemoveLogsDialogState(true, message)
                }
            },
            columnsContextMenuCallbacks = viewModel.columnsContextMenuCallbacks,
            onShowVirtualDeviceClicked = {
                viewModel.devicePreviewsDialogState.value = true
            },
            onColumnResized = viewModel::onColumnResized,
        )
    }
}