package com.alekso.dltstudio.com.alekso.dltstudio.logs

import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.alekso.dltstudio.model.Column
import com.alekso.dltstudio.model.ColumnParams

interface ColumnsContextMenuCallbacks {
    fun onToggleColumnVisibility(key: Column, checked: Boolean)

    companion object {
        val Stub = object : ColumnsContextMenuCallbacks {
            override fun onToggleColumnVisibility(key: Column, checked: Boolean) = Unit
        }
    }
}

@Composable
fun ColumnsContextMenu(
    columnParams: SnapshotStateList<ColumnParams>,
    rowContextMenuCallbacks: ColumnsContextMenuCallbacks,
    content: @Composable () -> Unit
) {
    val menuItems = columnParams.map { params ->
        val visibilityTitle = if (params.visible) "Hide" else "Show"
        ContextMenuItem("$visibilityTitle '${params.column.menuName}'") {
            rowContextMenuCallbacks.onToggleColumnVisibility(params.column, !params.visible)
        }
    }

    ContextMenuArea(items = { menuItems }) {
        content()
    }
}