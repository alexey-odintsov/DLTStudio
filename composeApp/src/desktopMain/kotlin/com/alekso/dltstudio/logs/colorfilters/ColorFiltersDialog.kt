package com.alekso.dltstudio.logs.colorfilters

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.rememberDialogState
import com.alekso.dltstudio.logs.CellStyle
import com.alekso.dltstudio.ui.CustomButton
import com.alekso.dltstudio.ui.CustomCheckbox
import com.alekso.dltstudio.ui.ImageButton


@Composable
fun ColorFiltersDialog(
    visible: Boolean,
    onDialogClosed: () -> Unit,
    colorFilters: List<ColorFilter>,
    onColorFilterUpdate: (Int, ColorFilter) -> Unit,
    onColorFilterDelete: (Int) -> Unit,
) {
    DialogWindow(
        visible = visible, onCloseRequest = onDialogClosed,
        title = "Color Filters",
        state = rememberDialogState(width = 500.dp, height = 500.dp)
    ) {
        val editDialogState = remember { mutableStateOf(EditDialogState(false)) }

        if (editDialogState.value.visible) {
            EditColorFilterDialog(
                visible = editDialogState.value.visible,
                onDialogClosed = { editDialogState.value = EditDialogState(false) },
                colorFilter = editDialogState.value.filter,
                colorFilterIndex = editDialogState.value.filterIndex,
                onFilterUpdate = { i, filter ->
                    editDialogState.value.filter = filter
                    onColorFilterUpdate(i, filter)
                }
            )
        }

        ColorFiltersPanel(
            colorFilters,
            { i, filter -> editDialogState.value = EditDialogState(true, filter, i) },
            { i, f -> onColorFilterUpdate(i, f) },
            { i -> onColorFilterDelete(i) })
    }
}

@Composable
fun ColorFiltersPanel(
    colorFilters: List<ColorFilter>,
    onEditFilterClick: (Int, ColorFilter) -> Unit,
    onFilterUpdate: (Int, ColorFilter) -> Unit,
    onFilterDelete: (Int) -> Unit
) {

    Column(modifier = Modifier.padding(4.dp)) {
        LazyColumn {
            items(colorFilters.size) { i ->
                val filter = colorFilters[i]
                Row(
                    Modifier.padding(horizontal = 4.dp, vertical = 0.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    var checked by remember { mutableStateOf(filter.enabled) }
                    CustomCheckbox(
                        checked = checked,
                        modifier = Modifier.padding(horizontal = 8.dp),
                        onCheckedChange = {
                            checked = !checked
                            onFilterUpdate(i, filter.copy(enabled = checked))
                        }
                    )

                    Box(
                        modifier = Modifier.weight(1f)
                            .background(
                                color = filter.cellStyle.backgroundColor ?: Color.Transparent
                            )
                            .padding(horizontal = 4.dp)
                    ) {
                        Text(
                            text = filter.name,
                            modifier = Modifier.fillMaxWidth(),
                            color = filter.cellStyle.textColor ?: Color.Black
                        )
                    }

                    ImageButton(modifier = Modifier.size(28.dp),
                        iconName = "icon_edit.xml",
                        title = "Edit",
                        onClick = { onEditFilterClick(i, filter) })

                    ImageButton(modifier = Modifier.size(28.dp),
                        iconName = "icon_delete.xml",
                        title = "Delete",
                        onClick = { onFilterDelete(i) })
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
            mapOf(FilterParameter.ContextId to FilterCriteria("TC", TextCriteria.PlainText)),
            CellStyle(backgroundColor = Color.Gray, textColor = Color.White)
        ),
    )

    ColorFiltersPanel(colorFilters, { i, f -> }, { i, f -> }, { i -> })
}