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
import dltstudio.composeapp.generated.resources.Res
import dltstudio.composeapp.generated.resources.icon_w
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun ToggleImageButton(
    modifier: Modifier = Modifier.size(32.dp),
    checkedState: Boolean,
    icon: DrawableResource,
    title: String,
    updateCheckedState: (Boolean) -> Unit,
    checkedTintColor: Color = Color.DarkGray,
    unCheckedTintColor: Color = Color.Gray,
) {
    IconToggleButton(modifier = modifier,
        checked = checkedState,
        onCheckedChange = { updateCheckedState.invoke(it) }) {
        Image(
            painterResource(icon),
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
        icon = Res.drawable.icon_w,
        title = "Title",
        checkedTintColor = Color.Red,
        updateCheckedState = {}
    )
}