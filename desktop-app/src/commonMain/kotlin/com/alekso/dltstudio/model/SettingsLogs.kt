package com.alekso.dltstudio.model

import com.alekso.dltmessage.PayloadStorageType
import com.alekso.dltstudio.db.settings.SettingsLogsEntity
import java.io.File

data class SettingsLogs(
    val backendType: PayloadStorageType,
    val defaultLogsFolderPath: String? = null,
    val defaultColorFiltersFolderPath: String? = null,
) {
    companion object {
        val Default = SettingsLogs(
            backendType = PayloadStorageType.Binary,
        )

        fun getBackendById(id: Int): PayloadStorageType {
            return PayloadStorageType.entries.firstOrNull { it.id == id }
                ?: PayloadStorageType.Binary
        }

        fun getIdByBackend(backendType: PayloadStorageType): Int {
            return PayloadStorageType.entries.firstOrNull { it == backendType }?.id
                ?: Default.backendType.id
        }
    }

    val defaultLogsFolder: File?
        get() = defaultLogsFolderPath?.let { File(it) }

    val defaultColorFiltersFolder: File?
        get() = defaultColorFiltersFolderPath?.let { File(it) }
}

fun SettingsLogsEntity.toSettingsLogs(): SettingsLogs =
    SettingsLogs(
        SettingsLogs.getBackendById(backendType),
        defaultLogsFolderPath,
        defaultColorFiltersFolderPath
    )

fun SettingsLogs.toSettingsLogsEntity(): SettingsLogsEntity =
    SettingsLogsEntity(
        SettingsLogs.getIdByBackend(backendType),
        defaultLogsFolderPath,
        defaultColorFiltersFolderPath
    )
