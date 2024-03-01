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
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.rememberDialogState
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
        state = rememberDialogState(width = 700.dp, height = 500.dp)
    ) {
        EditColorFilterPanel(colorFilter, colorFilterIndex, onFilterUpdate, onDialogClosed)
    }
}

private val COL_NAME_SIZE_DP = 150.dp
private val SEARCH_INPUT_SIZE_DP = 250.dp
private val FILTER_TYPE = 150.dp

@Composable
fun EditColorFilterPanel(
    filter: ColorFilter,
    colorFilterIndex: Int,
    onFilterUpdate: (Int, ColorFilter) -> Unit,
    onDialogClosed: () -> Unit
) {
    var filterName by rememberSaveable { mutableStateOf(filter.name) }
    var messageType by rememberSaveable { mutableStateOf(filter.filters[FilterParameter.MessageType]?.value) }
    var messageTypeInfo by rememberSaveable { mutableStateOf(filter.filters[FilterParameter.MessageTypeInfo]?.value) }
    var ecuId by rememberSaveable { mutableStateOf(filter.filters[FilterParameter.EcuId]?.value) }
    var appId by rememberSaveable { mutableStateOf(filter.filters[FilterParameter.AppId]?.value) }
    var contextId by rememberSaveable { mutableStateOf(filter.filters[FilterParameter.ContextId]?.value) }
    var sessionId by rememberSaveable { mutableStateOf(filter.filters[FilterParameter.SessionId]?.value) }
    var payload by rememberSaveable { mutableStateOf(filter.filters[FilterParameter.Payload]?.value) }
    var payloadCriteria by rememberSaveable { mutableStateOf(filter.filters[FilterParameter.Payload]?.textCriteria) }
    val colNameStyle = Modifier.width(COL_NAME_SIZE_DP).padding(horizontal = 4.dp)
    var filterCellStyle by remember { mutableStateOf(filter.cellStyle) }

    val backgroundColorPickerDialogState = remember { mutableStateOf(false) }
    val textColorPickerDialogState = remember { mutableStateOf(false) }

    if (backgroundColorPickerDialogState.value) {
        ColorPickerDialog(
            visible = backgroundColorPickerDialogState.value,
            onDialogClosed = { backgroundColorPickerDialogState.value = false },
            initialColor = filterCellStyle.backgroundColor ?: Color.Green,
            onColorUpdate = { newColor ->
                filterCellStyle = filterCellStyle.copy(backgroundColor = newColor)
                backgroundColorPickerDialogState.value = false
            }
        )
    }

    if (textColorPickerDialogState.value) {
        ColorPickerDialog(
            visible = textColorPickerDialogState.value,
            onDialogClosed = { textColorPickerDialogState.value = false },
            initialColor = filterCellStyle.textColor ?: Color.Black,
            onColorUpdate = { newColor ->
                filterCellStyle = filterCellStyle.copy(textColor = newColor)
                textColorPickerDialogState.value = false
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
            val backgroundColor = filterCellStyle.backgroundColor ?: Color.Transparent
            val textColor = filterCellStyle.textColor ?: Color.Black

            TextButton(onClick = { backgroundColorPickerDialogState.value = true }) {
                Text(
                    text = "Background color",
                    modifier = Modifier.height(20.dp).background(color = backgroundColor),
                    textAlign = TextAlign.Center,
                    color = textColor,
                )
            }
            TextButton(onClick = { textColorPickerDialogState.value = true }) {
                Text(
                    text = "Text color",
                    modifier = Modifier.height(20.dp).background(color = backgroundColor),
                    textAlign = TextAlign.Center,
                    color = textColor,
                )
            }
        }
        Row {
            val items = mutableListOf("Any")
            items.addAll(MessageInfo.MessageType.entries.map { it.name })
            var initialSelection =
                items.indexOfFirst { it == filter.filters[FilterParameter.MessageType]?.value }
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
            items.addAll(MessageInfo.MessageTypeInfo.entries.map { it.name })
            var initialSelection =
                items.indexOfFirst { it == filter.filters[FilterParameter.MessageTypeInfo]?.value }
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
            val items = mutableListOf<String>()
            items.addAll(TextCriteria.entries.map { it.name })
            var initialSelection = items.indexOfFirst { it == payloadCriteria?.name }
            if (initialSelection == -1) initialSelection = 0

            CustomDropDown(
                modifier = Modifier.width(FILTER_TYPE).padding(horizontal = 4.dp),
                items = items,
                initialSelectedIndex = initialSelection,
                onItemsSelected = { i ->
                    payloadCriteria = if (i > 0) {
                        TextCriteria.valueOf(items[i])
                    } else null
                }
            )

        }

        CustomButton(onClick = {
            val map = mutableMapOf<FilterParameter, FilterCriteria>()
            messageType?.let {
                map[FilterParameter.MessageType] = FilterCriteria(it, TextCriteria.PlainText)
            }
            messageTypeInfo?.let {
                map[FilterParameter.MessageTypeInfo] = FilterCriteria(it, TextCriteria.PlainText)
            }
            ecuId?.let {
                map[FilterParameter.EcuId] = FilterCriteria(it, TextCriteria.PlainText)
            }
            appId?.let {
                map[FilterParameter.AppId] = FilterCriteria(it, TextCriteria.PlainText)
            }
            contextId?.let {
                map[FilterParameter.ContextId] = FilterCriteria(it, TextCriteria.PlainText)
            }
            sessionId?.let {
                map[FilterParameter.SessionId] = FilterCriteria(it, TextCriteria.PlainText)
            }
            payload?.let {
                map[FilterParameter.Payload] =
                    FilterCriteria(it, payloadCriteria ?: TextCriteria.PlainText)
            }
            onFilterUpdate(
                colorFilterIndex,
                ColorFilter(
                    name = filterName,
                    filters = map,
                    cellStyle = filterCellStyle
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
        mapOf(FilterParameter.ContextId to FilterCriteria("TC", TextCriteria.PlainText)),
        CellStyle(backgroundColor = Color.Gray, textColor = Color.White)
    )

    Column(Modifier.background(Color(238, 238, 238))) {
        EditColorFilterPanel(filter, 0, { i, f -> }) {}
    }
}