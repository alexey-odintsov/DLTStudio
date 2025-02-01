package com.alekso.dltstudio.logs

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import com.alekso.dltstudio.logs.colorfilters.ColorFiltersDialog
import com.alekso.dltstudio.logs.infopanel.VirtualDevicesDialog
import com.alekso.dltstudio.model.LogMessage
import com.alekso.dltstudio.plugins.PanelState
import com.alekso.dltstudio.plugins.PluginPanel
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi


data class LogsPanelState(
    val colorFiltersDialogState: Boolean,
) : PanelState()

class LogsPlugin(
    private val viewModel: LogsViewModel,
    private val state: LogsPanelState,
) : PluginPanel {
    override fun getPanelName(): String = "Logs"
    override fun getPanelState(): PanelState = state

    @OptIn(ExperimentalSplitPaneApi::class)
    @Composable
    override fun renderPanel(modifier: Modifier, state: PanelState) {
        val clipboardManager = LocalClipboardManager.current

        if (viewModel.colorFiltersDialogState.value) {
            ColorFiltersDialog(
                visible = viewModel.colorFiltersDialogState.value,
                onDialogClosed = { viewModel.colorFiltersDialogState.value = false },
                colorFilters = viewModel.colorFilters,
                onColorFilterUpdate = { i, f -> viewModel.onColorFilterUpdate(i, f) },
                onColorFilterDelete = { viewModel.onColorFilterDelete(it) },
                onColorFilterMove = { i, o -> viewModel.onColorFilterMove(i, o) },
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


        LogsPanel(
            modifier = modifier,
            logMessages = viewModel.logMessages,
            searchState = viewModel.searchState.value,
            searchAutoComplete = viewModel.searchAutocomplete,
            logInsights = viewModel.logInsights,
            virtualDevices = viewModel.virtualDevices,
            searchResult = viewModel.searchResult,
            searchIndexes = viewModel.searchIndexes,
            colorFilters = viewModel.colorFilters,
            logsToolbarState = viewModel.logsToolbarState,
            logsToolbarCallbacks = viewModel.logsToolbarCallbacks,
            vSplitterState = viewModel.vSplitterState,
            hSplitterState = viewModel.hSplitterState,
            logsListState = viewModel.logsListState,
            logsListSelectedRow = viewModel.logsListSelectedRow.value,
            searchListSelectedRow = viewModel.searchListSelectedRow.value,
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
            onShowVirtualDeviceClicked = {
                viewModel.devicePreviewsDialogState.value = true
            })
    }
}