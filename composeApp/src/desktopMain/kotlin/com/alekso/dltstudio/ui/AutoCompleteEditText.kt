package com.alekso.dltstudio.ui

import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties

@Composable
fun AutoCompleteEditText(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    items: List<String>,
) {

    var expanded by remember { mutableStateOf(false) }
    Box {
        CustomEditText(
            value = value,
            onValueChange = {
                expanded = true
                onValueChange(it)
            },
            modifier = modifier,
            singleLine = singleLine,
            interactionSource = interactionSource,
        )

        val filteringOptions = items.filter { it.contains(value, ignoreCase = true) }
        if (filteringOptions.isNotEmpty()) {
            DropdownMenu(
                modifier = Modifier.focusable(false),
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                },
                properties = PopupProperties(focusable = false)
            ) {
                filteringOptions.forEach { selectionOption ->
                    DropdownMenuItem(
                        modifier = Modifier
                            .focusable(false)
                            .height(16.dp),
                        onClick = {
                            onValueChange(selectionOption)
                            expanded = false
                        }
                    ) {
                        Text(
                            text = selectionOption,
                            fontSize = 12.sp,
                            lineHeight = 12.sp,
                        )
                    }
                }
            }
        }
    }
}