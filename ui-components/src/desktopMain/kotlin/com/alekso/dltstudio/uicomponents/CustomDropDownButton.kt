package com.alekso.dltstudio.uicomponents

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dltstudio.resources.Res
import dltstudio.resources.icon_dropdown_arrow_down

data class DropDownItem(
    val title: String,
    val clickHandler: () -> Unit,
)

@Composable
fun CustomDropDownButton(
    modifier: Modifier = Modifier,
    items: List<DropDownItem>,
) {
    var expanded by remember { mutableStateOf(false) }

    CustomButton(
        modifier = modifier,
        onClick = items.first().clickHandler
    ) {
        Row {
            Text(text = items.first().title)
        }
        ImageButton(
            modifier = Modifier.size(20.dp).padding(0.dp),
            icon = if (expanded) {
                Res.drawable.icon_dropdown_arrow_down
            } else {
                Res.drawable.icon_dropdown_arrow_down
            },
            title = "Dropdown",
            onClick = { expanded = true },
            tintColor = MaterialTheme.colorScheme.onPrimary,
            tintable = true,
            paddingValues = PaddingValues(0.dp)
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    onClick = item.clickHandler,
                    text = { Text(item.title) }
                )
            }
        }
    }
}