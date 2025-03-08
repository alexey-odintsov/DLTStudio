package com.alekso.dltstudio.com.alekso.dltstudio.logs

import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.alekso.dltstudio.logs.ColumnsParams

interface ColumnsContextMenuCallbacks {
    fun onToggleColumnVisibility(index: Int, checked: Boolean)

    companion object {
        val Stub = object : ColumnsContextMenuCallbacks {
            override fun onToggleColumnVisibility(index: Int, checked: Boolean) = Unit
        }
    }
}

@Composable
fun ColumnsContextMenu(
    columnParams: SnapshotStateList<ColumnsParams>,
    rowContextMenuCallbacks: ColumnsContextMenuCallbacks,
    content: @Composable () -> Unit
) {
    val menuItems = columnParams.mapIndexed { index, params ->
        val visibilityTitle = if (params.visible) "Hide" else "Show"
        ContextMenuItem("$visibilityTitle '${params.name}'") {
            rowContextMenuCallbacks.onToggleColumnVisibility(index, !params.visible)
        }
    }

    ContextMenuArea(items = { menuItems }) {
        content()
    }
}