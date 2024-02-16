package com.alekso.dltstudio.logs.colorfilters

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogState
import androidx.compose.ui.window.DialogWindow
import com.alekso.dltstudio.logs.CellStyle
import com.alekso.dltstudio.ui.CustomButton
import com.alekso.dltstudio.ui.CustomCheckbox
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

    Column(modifier = Modifier.padding(4.dp)) {
        LazyColumn {
            items(colorFilters.size) { i ->
                val filter = colorFilters[i]
                Row(
                    Modifier.padding(horizontal = 4.dp, vertical = 0.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    var checked by remember { mutableStateOf(true) }
                    CustomCheckbox(
                        checked = checked,
                        modifier = Modifier.padding(horizontal = 8.dp),
                        onCheckedChange = { checked = !checked }
                    )

                    Box(
                        modifier = Modifier.background(
                            color = filter.cellStyle.backgroundColor ?: Color.Transparent
                        ).padding(horizontal = 4.dp)
                    ) {
                        Text(
                            text = filter.name,
                            modifier = Modifier.width(COL_FILTER_NAME_WIDTH_DP),
                            color = filter.cellStyle.textColor ?: Color.Black
                        )
                    }

                    ImageButton(modifier = Modifier.size(28.dp),
                        iconName = "icon_edit.xml",
                        title = "Edit",
                        onClick = { onEditFilterClick(i, filter) })
                }
            }
        }

        CustomButton(
            onClick = {
                onEditFilterClick(
                    -1,
                    ColorFilter("New filter", mutableMapOf(), CellStyle.Default)
                )
            },
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