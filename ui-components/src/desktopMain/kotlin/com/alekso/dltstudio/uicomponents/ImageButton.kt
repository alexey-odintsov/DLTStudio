package com.alekso.dltstudio.uicomponents

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    tintColor: Color = MaterialTheme.colorScheme.onSurface,
    paddingValues: PaddingValues = PaddingValues(6.dp)
) {
    IconButton(modifier = modifier, onClick = onClick) {
        Image(
            painterResource(icon),
            contentDescription = title,
            modifier = Modifier.padding(paddingValues),
            colorFilter = if (tintable) ColorFilter.tint(tintColor) else null
        )
    }
}