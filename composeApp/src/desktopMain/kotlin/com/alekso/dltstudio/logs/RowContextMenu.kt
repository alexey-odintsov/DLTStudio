package com.alekso.dltstudio.logs

import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.runtime.Composable

// TODO: Implement menu
@Composable
fun RowContextMenu(content: @Composable () -> Unit) {
    ContextMenuArea(items = {
        listOf(
            ContextMenuItem("Copy") {/*do something here*/},
            ContextMenuItem("Mark") {/*do something else*/}
        )
    }) {
        content()
    }
}