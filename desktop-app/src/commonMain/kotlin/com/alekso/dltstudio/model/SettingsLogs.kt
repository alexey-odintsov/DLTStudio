package com.alekso.dltstudio.model

import com.alekso.dltmessage.PayloadStorageType
import com.alekso.dltstudio.db.settings.SettingsLogsEntity

data class SettingsLogs(
    val backendType: PayloadStorageType,
) {
    companion object {
        val Default = SettingsLogs(backendType = PayloadStorageType.Binary)
        fun getBackendById(id: Int): PayloadStorageType {
            return PayloadStorageType.entries.firstOrNull { it.id == id }
                ?: PayloadStorageType.Binary
        }

        fun getIdByBackend(backendType: PayloadStorageType): Int {
            return PayloadStorageType.entries.firstOrNull { it == backendType }?.id
                ?: Default.backendType.id
        }
    }
}

fun SettingsLogsEntity.toSettingsLogs(): SettingsLogs =
    SettingsLogs(SettingsLogs.getBackendById(id))

fun SettingsLogs.toSettingsLogsEntity(): SettingsLogsEntity =
    SettingsLogsEntity(SettingsLogs.getIdByBackend(backendType))
