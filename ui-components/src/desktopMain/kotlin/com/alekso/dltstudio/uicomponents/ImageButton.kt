package com.alekso.dltstudio.uicomponents

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.material.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun ImageButton(
    modifier: Modifier = Modifier,
    icon: DrawableResource,
    title: String,
    onClick: () -> Unit,
    tintable: Boolean = true,
) {
    IconButton(modifier = modifier, onClick = onClick) {
        Image(
            painterResource(icon),
            contentDescription = title,
            modifier = Modifier.padding(6.dp),
            colorFilter = if (tintable) ColorFilter.tint(MaterialTheme.colorScheme.onSurface) else null
        )
    }
}