package com.alekso.dltstudio.logs

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.alekso.dltstudio.MainViewModel
import com.alekso.dltstudio.logs.colorfilters.ColorFiltersDialog
import com.alekso.dltstudio.plugins.contract.MessagesRepository
import com.alekso.dltstudio.plugins.contract.PluginPanel
import dltstudio.resources.Res
import dltstudio.resources.icon_upload
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi


class LogsPlugin(
    private val viewModel: MainViewModel,
    private val messagesRepository: MessagesRepository,
) : PluginPanel {
    override fun getPanelName(): String = "Logs"

    @OptIn(ExperimentalSplitPaneApi::class)
    @Composable
    override fun renderPanel(modifier: Modifier) {
        val messages = messagesRepository.getMessages().collectAsState()
        val searchResults = messagesRepository.getSearchResults().collectAsState()
        val markedIds = messagesRepository.getMarkedIds().collectAsState()
        val focusedMarkedIdIndex = messagesRepository.getFocusedMarkedIdIndex().collectAsState()
        val comments = messagesRepository.getComments().collectAsState()
        val colorFilters = viewModel.getColorFilters().collectAsState()
        val previewPanels = viewModel.previewPanels.collectAsState()
        val logSelection = viewModel.logSelection.collectAsState()
        val searchAutoComplete = viewModel.searchAutocomplete.collectAsState()

        if (viewModel.changeOrderDialogState.value.visible) {
            ChangeLogsOrderDialog(
                state = viewModel.changeOrderDialogState.value,
                logsOrder = viewModel.logsOrder.value,
                onDialogClosed = viewModel::onChangeOrderDialogStateClosed,
                onLogsOrderChanged = viewModel::onLogsOrderChanged)
        }

        if (viewModel.colorFiltersDialogState.value) {
            ColorFiltersDialog(
                visible = viewModel.colorFiltersDialogState.value,
                onDialogClosed = { viewModel.colorFiltersDialogState.value = false },
                colorFilters = colorFilters.value,
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

        if (messages.value.isEmpty()) {
            NoLogsStub()
        } else {
            LogsPanel(
                modifier = modifier,
                previewPanels = previewPanels.value,
                columnParams = viewModel.columnParams,
                logMessages = messages.value,
                markedIds = markedIds.value,
                searchState = viewModel.searchState.value,
                searchAutoComplete = searchAutoComplete.value,
                searchResult = searchResults.value,
                colorFilters = colorFilters.value,
                logsToolbarState = viewModel.logsToolbarState,
                logsToolbarCallbacks = viewModel.logsToolbarCallbacks,
                vSplitterState = viewModel.vSplitterState,
                hSplitterState = viewModel.hSplitterState,
                logsListState = viewModel.logsListState,
                logSelection = logSelection.value,
                selectedMessage = messagesRepository.getSelectedMessage().value,
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
                focusedBookmarkId = focusedMarkedIdIndex.value,
                comments = comments.value,
            )
        }
    }

    @Composable
    private fun NoLogsStub() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painterResource(Res.drawable.icon_upload),
                    contentDescription = "Drag and Drop log file(s) here",
                    modifier = Modifier.padding(6.dp).size(60.dp),
                    colorFilter = ColorFilter.tint(Color.Gray),
                )
                Text(text = "No logs are currently loaded.")
                Text("To begin, drag and drop your log file(s) into this window or use menu File - Open.")
            }
        }
    }
}