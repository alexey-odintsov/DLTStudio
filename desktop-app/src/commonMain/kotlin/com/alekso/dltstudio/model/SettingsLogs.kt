package com.alekso.dltstudio.model

import com.alekso.dltmessage.PayloadStorageType
import com.alekso.dltstudio.db.settings.SettingsLogsEntity

data class SettingsLogs(
    val backendType: PayloadStorageType,
) {
    companion object {
        val Default = SettingsLogs(backendType = PayloadStorageType.Binary)
    }
}

fun SettingsLogsEntity.toSettingsLogs(): SettingsLogs =
    SettingsLogs(PayloadStorageType.entries.firstOrNull { it.id == id }
        ?: PayloadStorageType.Binary)

fun SettingsLogs.toSettingsLogsEntity(): SettingsLogsEntity =
    SettingsLogsEntity(
        backendType = PayloadStorageType.entries.firstOrNull { it == backendType }?.id ?: 2
    )