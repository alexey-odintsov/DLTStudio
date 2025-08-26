package com.alekso.dltstudio.uicomponents

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
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
import com.alekso.dltstudio.theme.SystemTheme
import com.alekso.dltstudio.theme.ThemeManager
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
        contentPadding = PaddingValues(start = 8.dp, top = 2.dp, bottom = 2.dp, end = 2.dp),
        onClick = items.first().clickHandler
    ) {
        Row {
            Text(text = items.first().title)
        }
        Spacer(
            modifier = Modifier.padding(start = 8.dp).width(1.dp).height(24.dp)
                .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.12f))
        )
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
            modifier = Modifier.wrapContentSize().background(MaterialTheme.colorScheme.background),
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    modifier = Modifier.height(24.dp),
                    onClick = item.clickHandler,
                    text = { Text(item.title) }
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewCustomDropDownButtonTheme() {
    Column {
        ThemeManager.CustomTheme(SystemTheme(true)) {
            PreviewCustomDropDownButton()
        }
        ThemeManager.CustomTheme(SystemTheme(false)) {
            PreviewCustomDropDownButton()
        }
    }
}

@Composable
fun PreviewCustomDropDownButton() {
    Row {
        CustomDropDownButton(items = listOf(DropDownItem("Save", {}), DropDownItem("Save As", {})))
        CustomDropDownButton(items = listOf(DropDownItem("A", {}), DropDownItem("B", {})))
    }
}