package com.alekso.dltstudio.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Panel(modifier: Modifier = Modifier, title: String, content: @Composable () -> Unit) {
    Column(modifier = modifier) {
        Header(text = title)
        Divider()
        content()
    }
}

@Composable
private fun Header(modifier: Modifier = Modifier, text: String) {
    Text(
        modifier = modifier.padding(start = 4.dp, top = 4.dp, end = 4.dp, bottom = 2.dp),
        fontWeight = FontWeight(600),
        fontSize = 13.sp,
        text = text
    )
}