package alexey.odintsov.dltstudio.tests

import alexey.odintsov.dltmessage.PayloadStorageType
import alexey.odintsov.dltstudio.db.settings.SettingsLogsEntity
import alexey.odintsov.dltstudio.model.SettingsLogs
import alexey.odintsov.dltstudio.model.toSettingsLogs
import alexey.odintsov.dltstudio.model.toSettingsLogsEntity
import org.junit.Assert
import org.junit.Test

class SettingsTests {

    @Test
    fun `SettingsUI to entity conversion test Structured`() {
        val settings = SettingsLogs(backendType = PayloadStorageType.Structured)
        val settingsConverted = settings.toSettingsLogsEntity()
        val settingsEntity =
            SettingsLogsEntity(backendType = PayloadStorageType.Structured.id, id = 0)
        Assert.assertTrue(
            "\n$settingsConverted\n!=\n$settingsEntity",
            settingsConverted == settingsEntity
        )
    }

    @Test
    fun `SettingsUI to entity  conversion test Binary`() {
        val settings = SettingsLogs(backendType = PayloadStorageType.Binary)
        val settingsConverted = settings.toSettingsLogsEntity()
        val settingsEntity = SettingsLogsEntity(backendType = PayloadStorageType.Binary.id, id = 0)
        Assert.assertTrue(
            "\n$settingsConverted\n!=\n$settingsEntity",
            settingsConverted == settingsEntity
        )
    }

    @Test
    fun `SettingsUI to entity  conversion test Plain`() {
        val settings = SettingsLogs(backendType = PayloadStorageType.Plain)
        val settingsConverted = settings.toSettingsLogsEntity()
        val settingsEntity = SettingsLogsEntity(backendType = PayloadStorageType.Plain.id, id = 0)
        Assert.assertTrue(
            "\n$settingsConverted\n!=\n$settingsEntity",
            settingsConverted == settingsEntity
        )
    }


    @Test
    fun `SettingsUIEntity to model conversion test Structured`() {
        val settings = SettingsLogs(backendType = PayloadStorageType.Structured)
        val settingsEntity =
            SettingsLogsEntity(backendType = PayloadStorageType.Structured.id, id = 0)
        val settingsConverted = settingsEntity.toSettingsLogs()
        Assert.assertTrue(
            "\n$settingsConverted\n!=\n$settings",
            settingsConverted == settings
        )
    }

    @Test
    fun `SettingsUIEntity to model conversion test Binary`() {
        val settings = SettingsLogs(backendType = PayloadStorageType.Binary)
        val settingsEntity = SettingsLogsEntity(backendType = PayloadStorageType.Binary.id, id = 0)
        val settingsConverted = settingsEntity.toSettingsLogs()
        Assert.assertTrue(
            "\n$settingsConverted\n!=\n$settings",
            settingsConverted == settings
        )
    }

    @Test
    fun `SettingsUIEntity to model conversion test Plain`() {
        val settings = SettingsLogs(backendType = PayloadStorageType.Plain)
        val settingsEntity = SettingsLogsEntity(backendType = PayloadStorageType.Plain.id, id = 0)
        val settingsConverted = settingsEntity.toSettingsLogs()
        Assert.assertTrue(
            "\n$settingsConverted\n!=\n$settings",
            settingsConverted == settings
        )
    }
}