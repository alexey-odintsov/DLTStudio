package com.alekso.dltstudio.logs

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.alekso.dltstudio.MainViewModel
import com.alekso.dltstudio.logs.colorfilters.ColorFiltersDialog
import com.alekso.dltstudio.plugins.contract.MessagesRepository
import com.alekso.dltstudio.plugins.contract.PluginPanel
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi


class LogsPlugin(
    private val viewModel: MainViewModel,
    private val messagesRepository: MessagesRepository,
) : PluginPanel {
    override fun getPanelName(): String = "Logs"

    @OptIn(ExperimentalSplitPaneApi::class)
    @Composable
    override fun renderPanel(modifier: Modifier) {
        if (viewModel.colorFiltersDialogState.value) {
            ColorFiltersDialog(
                visible = viewModel.colorFiltersDialogState.value,
                onDialogClosed = { viewModel.colorFiltersDialogState.value = false },
                colorFilters = viewModel.colorFilters,
                callbacks = viewModel.colorFiltersDialogCallbacks,
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
            previewPanels = viewModel.previewPanels,
            columnParams = viewModel.columnParams,
            logMessages = messagesRepository.getMessages(),
            searchState = viewModel.searchState.value,
            searchAutoComplete = viewModel.searchAutocomplete,
            searchResult = messagesRepository.getSearchResults(),
            searchIndexes = messagesRepository.getSearchIndexes(),
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
            rowContextMenuCallbacks = viewModel.rowContextMenuCallbacks,
            columnsContextMenuCallbacks = viewModel.columnsContextMenuCallbacks,
            onColumnResized = viewModel::onColumnResized,
        )
    }
}