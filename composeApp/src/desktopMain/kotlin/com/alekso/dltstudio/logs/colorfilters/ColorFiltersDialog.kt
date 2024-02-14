package com.alekso.dltstudio.logs.colorfilters

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ButtonDefaults.textButtonColors
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogState
import androidx.compose.ui.window.DialogWindow
import com.alekso.dltstudio.logs.CellStyle
import com.alekso.dltstudio.ui.ImageButton

private val COL_FILTER_NAME_WIDTH_DP = 200.dp

@Composable
fun ColorFiltersDialog(
    visible: Boolean,
    onDialogClosed: () -> Unit,
    colorFilters: List<ColorFilter>,
    onFilterUpdate: (Int, ColorFilter) -> Unit
) {
    DialogWindow(
        visible = visible, onCloseRequest = onDialogClosed,
        title = "Color Filters",
        state = DialogState(width = 700.dp, height = 500.dp)
    ) {
        val editDialogState = remember { mutableStateOf(EditDialogState(false)) }

        if (editDialogState.value.visible) {
            EditColorFilterDialog(
                visible = editDialogState.value.visible,
                onDialogClosed = { editDialogState.value = EditDialogState(false) },
                colorFilter = editDialogState.value.filter,
                colorFilterIndex = editDialogState.value.filterIndex,
                onFilterUpdate = onFilterUpdate
            )
        }

        ColorFiltersPanel(colorFilters, { i, filter ->
            editDialogState.value = EditDialogState(true, filter, i)
        })
    }
}

@Composable
fun ColorFiltersPanel(
    colorFilters: List<ColorFilter>,
    onEditFilterClick: (Int, ColorFilter) -> Unit
) {

    Column {
        LazyColumn {
            items(colorFilters.size) { i ->
                val filter = colorFilters[i]
                Row(
                    Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "text",
                        modifier = Modifier.width(40.dp)
                            .background(
                                color = filter.cellStyle.backgroundColor ?: Color.Transparent
                            ),
                        textAlign = TextAlign.Center,
                        color = filter.cellStyle.textColor ?: Color.Black
                    )
                    Text(
                        text = filter.name,
                        Modifier.width(COL_FILTER_NAME_WIDTH_DP).padding(horizontal = 4.dp)
                    )
                    ImageButton(modifier = Modifier.size(24.dp),
                        iconName = "icon_edit.xml",
                        title = "Edit",
                        onClick = { onEditFilterClick(i, filter) })
                }
            }
        }
        TextButton(
            onClick = {
                onEditFilterClick(
                    -1,
                    ColorFilter("New filter", mutableMapOf(), CellStyle.Default)
                )
            },
            modifier = Modifier,
            colors = textButtonColors(backgroundColor = Color.LightGray)
        ) {
            Text("Add filter")
        }
    }
}

@Preview
@Composable
fun PreviewColorFiltersDialog() {
    val colorFilters = mutableListOf(
        ColorFilter("Filter1", mapOf(), CellStyle(backgroundColor = Color.Yellow)),
        ColorFilter("Memory", mapOf(), CellStyle(backgroundColor = Color.Green)),
        ColorFilter(
            "SIP",
            mapOf(FilterParameter.ContextId to "TC"),
            CellStyle(backgroundColor = Color.Gray, textColor = Color.White)
        ),
    )

    ColorFiltersPanel(colorFilters, { i, f -> })
}