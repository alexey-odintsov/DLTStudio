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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.rememberDialogState
import com.alekso.dltstudio.logs.CellStyle
import com.alekso.dltstudio.logs.filtering.FilterCriteria
import com.alekso.dltstudio.logs.filtering.FilterParameter
import com.alekso.dltstudio.logs.filtering.TextCriteria
import com.alekso.dltstudio.ui.ImageButton
import com.alekso.dltstudio.uicomponents.CustomButton
import com.alekso.dltstudio.uicomponents.CustomCheckbox
import dltstudio.composeapp.generated.resources.Res
import dltstudio.composeapp.generated.resources.icon_delete
import dltstudio.composeapp.generated.resources.icon_down
import dltstudio.composeapp.generated.resources.icon_edit
import dltstudio.composeapp.generated.resources.icon_up

interface ColorFiltersDialogCallbacks {
    fun onColorFilterUpdate(position: Int, filter: ColorFilter)
    fun onColorFilterDelete(position: Int)
    fun onColorFilterMove(index: Int, offset: Int)
}

@Composable
fun ColorFiltersDialog(
    visible: Boolean,
    onDialogClosed: () -> Unit,
    colorFilters: List<ColorFilter>,
    callbacks: ColorFiltersDialogCallbacks,
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
                    callbacks.onColorFilterUpdate(i, filter)
                }
            )
        }

        ColorFiltersPanel(
            colorFilters,
            callbacks,
            { i, filter -> editDialogState.value = EditDialogState(true, filter, i) },
        )
    }
}

@Composable
fun ColorFiltersPanel(
    colorFilters: List<ColorFilter>,
    callbacks: ColorFiltersDialogCallbacks,
    onEditFilterClick: (Int, ColorFilter) -> Unit,
) {

    Column(modifier = Modifier.padding(4.dp)) {
        LazyColumn(Modifier.weight(1f)) {
            items(colorFilters.size) { i ->
                val filter = colorFilters[i]
                Row(
                    Modifier.padding(horizontal = 4.dp, vertical = 0.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    ImageButton(modifier = Modifier.size(28.dp),
                        icon = Res.drawable.icon_up,
                        title = "Move Up",
                        onClick = { callbacks.onColorFilterMove(i, -1) })

                    ImageButton(modifier = Modifier.size(28.dp),
                        icon = Res.drawable.icon_down,
                        title = "Move Down",
                        onClick = { callbacks.onColorFilterMove(i, 1) })

                    CustomCheckbox(
                        checked = filter.enabled,
                        modifier = Modifier.padding(horizontal = 8.dp),
                        onCheckedChange = {
                            callbacks.onColorFilterUpdate(i, filter.copy(enabled = !filter.enabled))
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
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = filter.cellStyle.textColor ?: Color.Black
                        )
                    }

                    ImageButton(modifier = Modifier.size(28.dp),
                        icon = Res.drawable.icon_edit,
                        title = "Edit",
                        onClick = { onEditFilterClick(i, filter) })

                    ImageButton(modifier = Modifier.size(28.dp),
                        icon = Res.drawable.icon_delete,
                        title = "Delete",
                        onClick = { callbacks.onColorFilterDelete(i) })
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
            "Long long long long long long long long long long long filter name",
            mapOf(FilterParameter.ContextId to FilterCriteria("TC", TextCriteria.PlainText)),
            CellStyle(backgroundColor = Color.Gray, textColor = Color.White)
        ),
    )
    val callbacks = object : ColorFiltersDialogCallbacks {
        override fun onColorFilterUpdate(position: Int, filter: ColorFilter) = Unit
        override fun onColorFilterDelete(position: Int) = Unit
        override fun onColorFilterMove(index: Int, offset: Int) = Unit
    }
    ColorFiltersPanel(colorFilters, callbacks, { _, _ -> })
}