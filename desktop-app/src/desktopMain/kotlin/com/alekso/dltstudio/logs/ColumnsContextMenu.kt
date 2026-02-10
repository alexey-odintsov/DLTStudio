package com.alekso.dltstudio.logs

import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.runtime.Composable
import com.alekso.dltstudio.model.Column
import com.alekso.dltstudio.model.ColumnParams

interface ColumnsContextMenuCallbacks {
    fun onToggleColumnVisibility(key: Column, checked: Boolean)
    fun onResetParams()

    companion object {
        val Stub = object : ColumnsContextMenuCallbacks {
            override fun onToggleColumnVisibility(key: Column, checked: Boolean) = Unit
            override fun onResetParams() = Unit
        }
    }
}

@Composable
fun ColumnsContextMenu(
    columnParams: List<ColumnParams>,
    rowContextMenuCallbacks: ColumnsContextMenuCallbacks,
    content: @Composable () -> Unit
) {
    val menuItems = columnParams.map { params ->
        val visibilityTitle = if (params.visible) "Hide" else "Show"
        ContextMenuItem("$visibilityTitle '${params.column.menuName}'") {
            rowContextMenuCallbacks.onToggleColumnVisibility(params.column, !params.visible)
        }
    }.toMutableList()

    menuItems.add(
        ContextMenuItem("Reset columns") {
            rowContextMenuCallbacks.onResetParams()
        }
    )

    ContextMenuArea(items = { menuItems }) {
        content()
    }
}