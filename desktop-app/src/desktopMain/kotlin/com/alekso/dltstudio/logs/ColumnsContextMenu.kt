package com.alekso.dltstudio.com.alekso.dltstudio.logs

import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.alekso.dltstudio.model.Columns
import com.alekso.dltstudio.model.ColumnsParams

interface ColumnsContextMenuCallbacks {
    fun onToggleColumnVisibility(key: Columns, checked: Boolean)

    companion object {
        val Stub = object : ColumnsContextMenuCallbacks {
            override fun onToggleColumnVisibility(key: Columns, checked: Boolean) = Unit
        }
    }
}

@Composable
fun ColumnsContextMenu(
    columnParams: SnapshotStateList<ColumnsParams>,
    rowContextMenuCallbacks: ColumnsContextMenuCallbacks,
    content: @Composable () -> Unit
) {
    val menuItems = columnParams.map { params ->
        val visibilityTitle = if (params.visible) "Hide" else "Show"
        ContextMenuItem("$visibilityTitle '${params.name}'") {
            rowContextMenuCallbacks.onToggleColumnVisibility(params.key, !params.visible)
        }
    }

    ContextMenuArea(items = { menuItems }) {
        content()
    }
}