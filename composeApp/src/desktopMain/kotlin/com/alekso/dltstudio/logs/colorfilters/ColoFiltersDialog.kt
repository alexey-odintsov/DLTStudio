package com.alekso.dltstudio.logs.colorfilters

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults.textButtonColors
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogState
import androidx.compose.ui.window.DialogWindow
import com.alekso.dltstudio.logs.CellStyle

@Composable
fun ColorFiltersDialog(
    visible: Boolean,
    onDialogClosed: () -> Unit,
    colorFilters: List<ColorFilter>
) {
    DialogWindow(
        visible = visible, onCloseRequest = onDialogClosed,
        title = "Color Filters",
        state = DialogState(width = 700.dp, height = 500.dp)
    ) {
        ColorFiltersPanel(colorFilters)
    }
}

@Composable
fun ColorFiltersPanel(colorFilters: List<ColorFilter>) {
    Column(Modifier.width(1000.dp)) {
        LazyColumn {
            items(colorFilters.size) { i ->
                val filter = colorFilters[i]
                Row(Modifier.padding(horizontal = 4.dp)) {
                    Text(
                        text = "text",
                        modifier = Modifier.width(40.dp).height(20.dp)
                            .background(
                                color = filter.cellStyle.backgroundColor ?: Color.Transparent
                            ),
                        textAlign = TextAlign.Center,
                        color = filter.cellStyle.textColor ?: Color.Black
                    )
                    Text(
                        text = filter.name,
                        Modifier.padding(horizontal = 4.dp)
                    )
                }
            }
        }
        TextButton(
            onClick = {},
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

    ColorFiltersPanel(colorFilters)
}