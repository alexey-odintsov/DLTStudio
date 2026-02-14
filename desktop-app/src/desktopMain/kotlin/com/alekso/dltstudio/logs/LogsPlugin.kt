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
        val selectedMessage = messagesRepository.getSelectedMessage().collectAsState()
        val columnParams = viewModel.columnParams.collectAsState()
        val logsToolbarState = viewModel.logsToolbarState.collectAsState()
        val colorFiltersDialogState = viewModel.colorFiltersDialogState.collectAsState()
        val removeLogsDialogState = viewModel.removeLogsDialogState.collectAsState()
        val changeOrderDialogState = viewModel.changeOrderDialogState.collectAsState()
        val logsOrder = viewModel.logsOrder.collectAsState()
        val searchState = viewModel.searchState.collectAsState()


        if (changeOrderDialogState.value.visible) {
            ChangeLogsOrderDialog(
                state = changeOrderDialogState.value,
                logsOrder = logsOrder.value,
                onDialogClosed = viewModel::onChangeOrderDialogStateClosed,
                onLogsOrderChanged = viewModel::onLogsOrderChanged)
        }

        if (colorFiltersDialogState.value) {
            ColorFiltersDialog(
                visible = colorFiltersDialogState.value,
                onDialogClosed = viewModel::closeColorFiltersDialog,
                colorFilters = colorFilters.value,
                callbacks = viewModel.colorFiltersDialogCallbacks,
            )
        }

        if (removeLogsDialogState.value.visible) {
            RemoveLogsDialog(
                visible = removeLogsDialogState.value.visible,
                message = removeLogsDialogState.value.message,
                onDialogClosed = viewModel::closeRemoveLogsDialog,
                onFilterClicked = viewModel::removeMessagesByFilters,
            )
        }

        if (messages.value.isEmpty()) {
            NoLogsStub()
        } else {
            LogsPanel(
                modifier = modifier,
                previewPanels = previewPanels.value,
                columnParams = columnParams.value,
                logMessages = messages.value,
                markedIds = markedIds.value,
                searchState = searchState.value,
                searchAutoComplete = searchAutoComplete.value,
                searchResult = searchResults.value,
                colorFilters = colorFilters.value,
                logsToolbarState = logsToolbarState.value,
                logsToolbarCallbacks = viewModel.logsToolbarCallbacks,
                vSplitterState = viewModel.vSplitterState,
                hSplitterState = viewModel.hSplitterState,
                logsListState = viewModel.logsListState,
                logSelection = logSelection.value,
                selectedMessage = selectedMessage.value,
                searchListState = viewModel.searchListState,
                onLogsRowSelected = viewModel::onLogsRowSelected,
                onSearchRowSelected = viewModel::onSearchRowSelected,
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