package com.alekso.dltstudio.plugins

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.alekso.dltstudio.model.contract.LogMessage

interface MessagesProvider {
    fun getMessages(): SnapshotStateList<LogMessage>
}