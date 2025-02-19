package com.alekso.dltstudio.model

import com.alekso.dltstudio.db.settings.SettingsUIEntity

data class SettingsUI(
    val fontSize: Int,
    val fontType: Int,
) {
    companion object {
        val Default = SettingsUI(
            fontSize = 12, fontType = 1
        )
    }
}

fun SettingsUIEntity.toSettingsUI() = SettingsUI(fontSize, fontType)

fun SettingsUI.toSettingsUIEntity() = SettingsUIEntity(fontSize = fontSize, fontType = fontType)