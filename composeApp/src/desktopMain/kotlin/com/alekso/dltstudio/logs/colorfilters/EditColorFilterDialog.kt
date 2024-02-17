package com.alekso.dltstudio.logs.colorfilters

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogState
import androidx.compose.ui.window.DialogWindow
import com.alekso.dltparser.dlt.MessageInfo
import com.alekso.dltstudio.colors.ColorPickerDialog
import com.alekso.dltstudio.logs.CellStyle
import com.alekso.dltstudio.ui.CustomButton
import com.alekso.dltstudio.ui.CustomDropDown
import com.alekso.dltstudio.ui.CustomEditText


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
    println("EditColorFilterDialog $colorFilter")
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
    var messageType by rememberSaveable { mutableStateOf(filter.filters[FilterParameter.MessageType]) }
    var messageTypeInfo by rememberSaveable { mutableStateOf(filter.filters[FilterParameter.MessageTypeInfo]) }
    var ecuId by rememberSaveable { mutableStateOf(filter.filters[FilterParameter.EcuId]) }
    var appId by rememberSaveable { mutableStateOf(filter.filters[FilterParameter.AppId]) }
    var contextId by rememberSaveable { mutableStateOf(filter.filters[FilterParameter.ContextId]) }
    var sessionId by rememberSaveable { mutableStateOf(filter.filters[FilterParameter.SessionId]) }
    var payload by rememberSaveable { mutableStateOf(filter.filters[FilterParameter.Payload]) }
    val colNameStyle = Modifier.width(COL_NAME_SIZE_DP).padding(horizontal = 4.dp)


    val colorPickerDialogState = remember { mutableStateOf(false) }

    if (colorPickerDialogState.value) {
        ColorPickerDialog(
            visible = colorPickerDialogState.value,
            onDialogClosed = { colorPickerDialogState.value = false },
            initialColor = filter.cellStyle.backgroundColor ?: Color.Green,
            onColorUpdate = { newColor ->
                println("Color $colorFilterIndex update: $newColor")
                onFilterUpdate(
                    colorFilterIndex,
                    filter.copy(cellStyle = filter.cellStyle.copy(backgroundColor = newColor))
                )
                colorPickerDialogState.value = false
            }
        )
    }

    Column(
        Modifier.width(1000.dp).padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(modifier = colNameStyle, text = "Name")
            CustomEditText(
                modifier = Modifier.width(SEARCH_INPUT_SIZE_DP),
                value = filterName, onValueChange = {
                    filterName = it
                }
            )
        }
        Row {
            Text(modifier = colNameStyle, text = "Color")
            val backgroundColor = filter.cellStyle.backgroundColor ?: Color.Transparent
            val textColor = filter.cellStyle.textColor ?: Color.Black
            println("ColorBox $backgroundColor")

            TextButton(onClick = {
                colorPickerDialogState.value = true
            }) {
                Text(
                    text = "Color",
                    modifier = Modifier.width(40.dp).height(20.dp)
                        .background(
                            color = backgroundColor
                        ),
                    textAlign = TextAlign.Center,
                    color = textColor,
                )
            }
        }
        Row {
            val items = mutableListOf("Any")
            items.addAll(MessageInfo.MESSAGE_TYPE.entries.map { it.name })
            var initialSelection =
                items.indexOfFirst { it == filter.filters[FilterParameter.MessageType] }
            if (initialSelection == -1) initialSelection = 0

            Text(modifier = colNameStyle, text = "Message Type")
            CustomDropDown(
                modifier = Modifier.width(SEARCH_INPUT_SIZE_DP).padding(horizontal = 4.dp),
                items = items,
                initialSelectedIndex = initialSelection,
                onItemsSelected = { i ->
                    messageType = if (i > 0) {
                        items[i]
                    } else null
                }
            )
        }

        Row {
            val items = mutableListOf("Any")
            items.addAll(MessageInfo.MESSAGE_TYPE_INFO.entries.map { it.name })
            var initialSelection =
                items.indexOfFirst { it == filter.filters[FilterParameter.MessageTypeInfo] }
            if (initialSelection == -1) initialSelection = 0

            Text(modifier = colNameStyle, text = "Message Type Info")
            CustomDropDown(
                modifier = Modifier.width(SEARCH_INPUT_SIZE_DP).padding(horizontal = 4.dp),
                items = items,
                initialSelectedIndex = initialSelection,
                onItemsSelected = { i ->
                    messageTypeInfo = if (i > 0) {
                        items[i]
                    } else null
                }
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(modifier = colNameStyle, text = "ECU ID")
            CustomEditText(
                modifier = Modifier.width(SEARCH_INPUT_SIZE_DP),
                value = ecuId ?: "", onValueChange = {
                    ecuId = it
                }
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(modifier = colNameStyle, text = "App ID")
            CustomEditText(
                modifier = Modifier.width(SEARCH_INPUT_SIZE_DP),
                value = appId ?: "", onValueChange = {
                    appId = it
                }
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(modifier = colNameStyle, text = "Context ID")
            CustomEditText(
                modifier = Modifier.width(SEARCH_INPUT_SIZE_DP),
                value = contextId ?: "", onValueChange = {
                    contextId = it
                }
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(modifier = colNameStyle, text = "Session ID")
            CustomEditText(
                modifier = Modifier.width(SEARCH_INPUT_SIZE_DP),
                value = sessionId ?: "", onValueChange = {
                    sessionId = it
                }
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(modifier = colNameStyle, text = "Payload")
            CustomEditText(
                modifier = Modifier.width(SEARCH_INPUT_SIZE_DP),
                value = payload ?: "", onValueChange = {
                    payload = it
                }
            )
        }

        CustomButton(onClick = {
            val map = mutableMapOf<FilterParameter, String>()
            messageType?.let {
                map[FilterParameter.MessageType] = it
            }
            messageTypeInfo?.let {
                map[FilterParameter.MessageTypeInfo] = it
            }
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

    Column(Modifier.background(Color(238, 238, 238))) {
        EditColorFilterPanel(filter, 0, { i, f -> }) {}
    }
}