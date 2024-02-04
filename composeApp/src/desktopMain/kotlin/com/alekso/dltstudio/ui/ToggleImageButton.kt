package com.alekso.dltstudio.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.IconToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun ToggleImageButton(
    modifier: Modifier = Modifier.size(32.dp),
    checkedState: Boolean,
    iconName: String,
    title: String,
    updateCheckedState: (Boolean) -> Unit,
    checkedTintColor: Color = Color.DarkGray,
    unCheckedTintColor: Color = Color.Gray,
) {
    IconToggleButton(modifier = modifier,
        checked = checkedState,
        onCheckedChange = { updateCheckedState.invoke(it) }) {
        Image(
            painterResource(iconName),
            contentDescription = title,
            modifier = Modifier.padding(6.dp),
            colorFilter = if (checkedState) {
                ColorFilter.tint(checkedTintColor)
            } else {
                ColorFilter.tint(unCheckedTintColor)
            }
        )
    }
}

@Preview
@Composable
fun PreviewToggleImageButton() {
    ToggleImageButton(checkedState = true,
        iconName = "icon_w.xml",
        title = "Title",
        checkedTintColor = Color.Red,
        updateCheckedState = {}
    )
}