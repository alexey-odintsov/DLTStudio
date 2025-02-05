package com.alekso.dltstudio.plugins

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.alekso.dltstudio.model.contract.LogMessage

interface DLTStudioPlugin {
    fun identify(): String
    fun init(logs: SnapshotStateList<LogMessage>, onProgressUpdate: (Float) -> Unit)
}