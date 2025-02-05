package com.alekso.dltstudio.ui

import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import com.alekso.dltstudio.uicomponents.CustomEditText

@Composable
fun AutoCompleteEditText(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    items: SnapshotStateList<String>,
) {

    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        CustomEditText(
            value = value,
            onValueChange = {
                expanded = true
                onValueChange(it)
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = singleLine,
            interactionSource = interactionSource,
        )

        val filteringOptions = items.filter { it.contains(value, ignoreCase = true) }
        if (filteringOptions.isNotEmpty()) {
            DropdownMenu(
                modifier = Modifier.focusable(false).fillMaxWidth().heightIn(0.dp, 200.dp),
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
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 12.sp,
                            lineHeight = 12.sp,
                        )
                    }
                }
            }
        }
    }
}