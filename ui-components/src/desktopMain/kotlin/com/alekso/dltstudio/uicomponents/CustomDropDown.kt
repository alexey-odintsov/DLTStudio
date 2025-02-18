package com.alekso.dltstudio.uicomponents

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CustomDropDown(
    modifier: Modifier, items: List<String>,
    initialSelectedIndex: Int,
    onItemsSelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableStateOf(initialSelectedIndex) }

    CompositionLocalProvider(
        // disable 48dp padding (minimumInteractiveComponentSize)
        LocalMinimumInteractiveComponentEnforcement provides false,
    ) {
        Box(
            modifier = modifier.border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                .background(Color.White, shape = RoundedCornerShape(4.dp))
        ) {
            Row(
                modifier = Modifier.clickable(onClick = { expanded = true }),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    items[selectedIndex],
                    modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                )
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 4.dp)
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = modifier
            ) {
                items.forEachIndexed { index, s ->
                    DropdownMenuItem(onClick = {
                        selectedIndex = index
                        expanded = false
                        onItemsSelected(selectedIndex)
                    }) {
                        Text(text = s)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewCustomDropDown() {
    CustomDropDown(Modifier.width(200.dp), items = listOf("a", "b", "c"),
        initialSelectedIndex = 1,
        { i -> })
}