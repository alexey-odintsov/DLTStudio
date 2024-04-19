package com.alekso.dltstudio.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun ImageButton(
    modifier: Modifier = Modifier,
    iconName: String,
    title: String,
    onClick: () -> Unit
) {
    IconButton(modifier = modifier, onClick = onClick) {
        Image(
            painterResource(DrawableResource(iconName)),
            contentDescription = title,
            modifier = Modifier.padding(6.dp),
        )
    }
}