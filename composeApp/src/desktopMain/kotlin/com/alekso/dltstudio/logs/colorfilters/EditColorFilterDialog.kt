package com.alekso.dltstudio.logs.colorfilters

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogState
import androidx.compose.ui.window.DialogWindow
import com.alekso.dltstudio.logs.CellStyle
import com.alekso.dltstudio.ui.SearchEditText


class EditDialogState(
    var visible: Boolean,
    var filter: ColorFilter = ColorFilter.Empty,
    val filterIndex: Int = -1
)

@Composable
fun EditColorFilterDialog(
    visible: Boolean,
    onDialogClosed: () -> Unit,
    colorFilter: ColorFilter,
    onFilterUpdate: (Int, ColorFilter) -> Unit,
    colorFilterIndex: Int,
) {
    DialogWindow(
        visible = visible, onCloseRequest = onDialogClosed,
        title = if (colorFilterIndex >= 0) "Edit Color Filter" else "Add new color filter",
        state = DialogState(width = 700.dp, height = 500.dp)
    ) {
        EditColorFilterPanel(colorFilter, colorFilterIndex, onFilterUpdate, onDialogClosed)
    }
}

private val COL_NAME_SIZE_DP = 150.dp
private val SEARCH_INPUT_SIZE_DP = 250.dp

@Composable
fun EditColorFilterPanel(
    filter: ColorFilter,
    colorFilterIndex: Int,
    onFilterUpdate: (Int, ColorFilter) -> Unit,
    onDialogClosed: () -> Unit
) {
    println("$colorFilterIndex $filter")
    var filterName by rememberSaveable { mutableStateOf(filter.name) }
    var ecuId by rememberSaveable { mutableStateOf(filter.filters[FilterParameter.EcuId]) }
    var appId by rememberSaveable { mutableStateOf(filter.filters[FilterParameter.AppId]) }
    var contextId by rememberSaveable { mutableStateOf(filter.filters[FilterParameter.ContextId]) }
    var sessionId by rememberSaveable { mutableStateOf(filter.filters[FilterParameter.SessionId]) }
    var payload by rememberSaveable { mutableStateOf(filter.filters[FilterParameter.Payload]) }
    val colNameStyle = Modifier.width(COL_NAME_SIZE_DP).padding(horizontal = 4.dp)

    Column(Modifier.width(1000.dp)) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(modifier = colNameStyle, text = "Name")
            SearchEditText(
                modifier = Modifier.width(SEARCH_INPUT_SIZE_DP).height(32.dp),
                value = filterName, onValueChange = {
                    filterName = it
                }
            )
        }
        Row {
            Text(modifier = colNameStyle, text = "Color")
            Text(
                text = "Color",
                modifier = Modifier.width(40.dp).height(20.dp)
                    .background(
                        color = filter.cellStyle.backgroundColor ?: Color.Transparent
                    ),
                textAlign = TextAlign.Center,
                color = filter.cellStyle.textColor ?: Color.Black
            )
        }
        Row {
            Text(modifier = colNameStyle, text = "Message Type")
            Text(modifier = colNameStyle, text = "Message Type")
        }

        Row {
            Text(modifier = colNameStyle, text = "Message Type Info")
            Text(modifier = colNameStyle, text = "Message Type Info")
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(modifier = colNameStyle, text = "ECU ID")
            SearchEditText(
                modifier = Modifier.width(SEARCH_INPUT_SIZE_DP).height(32.dp),
                value = ecuId ?: "", onValueChange = {
                    ecuId = it
                }
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(modifier = colNameStyle, text = "App ID")
            SearchEditText(
                modifier = Modifier.width(SEARCH_INPUT_SIZE_DP).height(32.dp),
                value = appId ?: "", onValueChange = {
                    appId = it
                }
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(modifier = colNameStyle, text = "Context ID")
            SearchEditText(
                modifier = Modifier.width(SEARCH_INPUT_SIZE_DP).height(32.dp),
                value = contextId ?: "", onValueChange = {
                    contextId = it
                }
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(modifier = colNameStyle, text = "Session ID")
            SearchEditText(
                modifier = Modifier.width(SEARCH_INPUT_SIZE_DP).height(32.dp),
                value = sessionId ?: "", onValueChange = {
                    sessionId = it
                }
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(modifier = colNameStyle, text = "Payload")
            SearchEditText(
                modifier = Modifier.width(SEARCH_INPUT_SIZE_DP).height(32.dp),
                value = payload ?: "", onValueChange = {
                    payload = it
                }
            )
        }

        TextButton(onClick = {
            val map = mutableMapOf<FilterParameter, String>()
            ecuId?.let {
                map[FilterParameter.EcuId] = it
            }
            appId?.let {
                map[FilterParameter.AppId] = it
            }
            contextId?.let {
                map[FilterParameter.ContextId] = it
            }
            sessionId?.let {
                map[FilterParameter.SessionId] = it
            }
            payload?.let {
                map[FilterParameter.Payload] = it
            }
            onFilterUpdate(
                colorFilterIndex,
                ColorFilter(
                    name = filterName,
                    filters = map,
                    cellStyle = filter.cellStyle
                )
            )
            onDialogClosed()
        }) {
            Text(text = if (colorFilterIndex >= 0) "Update" else "Add")
        }

    }
}

@Preview
@Composable
fun PreviewEditColorFilterDialog() {
    val filter = ColorFilter(
        "SIP",
        mapOf(FilterParameter.ContextId to "TC"),
        CellStyle(backgroundColor = Color.Gray, textColor = Color.White)
    )

    EditColorFilterPanel(filter, 0, { i, f -> }) {}
}